package com.onlinePharmacy.order.service;

import com.onlinePharmacy.order.client.CatalogClient;
import com.onlinePharmacy.order.client.UserClient;
import com.onlinePharmacy.order.dto.PrescriptionClientResponse;
import com.onlinePharmacy.order.dto.UserResponse;
import com.onlinePharmacy.order.dto.CheckoutRequest;
import com.onlinePharmacy.order.dto.OrderResponse;
import com.onlinePharmacy.order.entity.*;
import com.onlinePharmacy.order.exception.BadRequestException;
import com.onlinePharmacy.order.exception.InvalidOrderStateException;
import com.onlinePharmacy.order.exception.ResourceNotFoundException;
import com.onlinePharmacy.order.messaging.OrderEventPublisher;
import com.onlinePharmacy.order.repository.*;
import com.onlinePharmacy.order.util.OrderMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class OrderServiceImpl implements OrderService {

    private static final double DELIVERY_CHARGE     = 49.0;
    private static final double FREE_DELIVERY_ABOVE = 499.0;

    private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = new EnumMap<>(OrderStatus.class);

    static {
        VALID_TRANSITIONS.put(OrderStatus.CHECKOUT_STARTED,
                Set.of(OrderStatus.PRESCRIPTION_PENDING, OrderStatus.PAYMENT_PENDING, OrderStatus.CUSTOMER_CANCELLED));

        VALID_TRANSITIONS.put(OrderStatus.PRESCRIPTION_PENDING,
                Set.of(OrderStatus.PRESCRIPTION_APPROVED, OrderStatus.PRESCRIPTION_REJECTED));

        VALID_TRANSITIONS.put(OrderStatus.PRESCRIPTION_APPROVED, Set.of(OrderStatus.PAYMENT_PENDING));
        VALID_TRANSITIONS.put(OrderStatus.PRESCRIPTION_REJECTED, Set.of(OrderStatus.CUSTOMER_CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.PAYMENT_PENDING, Set.of(OrderStatus.PAID, OrderStatus.PAYMENT_FAILED, OrderStatus.CUSTOMER_CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.PAID, Set.of(OrderStatus.PACKED, OrderStatus.ADMIN_CANCELLED));
        VALID_TRANSITIONS.put(OrderStatus.PACKED, Set.of(OrderStatus.OUT_FOR_DELIVERY));
        VALID_TRANSITIONS.put(OrderStatus.OUT_FOR_DELIVERY, Set.of(OrderStatus.DELIVERED));
        VALID_TRANSITIONS.put(OrderStatus.DELIVERED, Set.of(OrderStatus.RETURN_REQUESTED));
        VALID_TRANSITIONS.put(OrderStatus.RETURN_REQUESTED, Set.of(OrderStatus.REFUND_INITIATED));
        VALID_TRANSITIONS.put(OrderStatus.REFUND_INITIATED, Set.of(OrderStatus.REFUND_COMPLETED));
    }

    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;
    private final CatalogClient catalogClient;
    private final OrderEventPublisher orderEventPublisher;
    private final UserClient userClient;

    public OrderServiceImpl(OrderRepository orderRepository,
                            CartItemRepository cartItemRepository,
                            AddressRepository addressRepository,
                            CartService cartService,
                            CatalogClient catalogClient,
                            OrderEventPublisher orderEventPublisher,
                            UserClient userClient) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.addressRepository = addressRepository;
        this.cartService = cartService;
        this.catalogClient = catalogClient;
        this.orderEventPublisher = orderEventPublisher;
        this.userClient = userClient;
    }

    @Override
    @Transactional
    public OrderResponse startCheckout(Long userId, CheckoutRequest request) {
        System.out.println("DEBUG: Starting checkout for User ID: " + userId);
        
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty. Add items before checkout.");
        }

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + request.getAddressId()));

        boolean hasRxItem = cartItems.stream()
                .anyMatch(i -> Boolean.TRUE.equals(i.getRequiresPrescription()));

        // --- PRESCRIPTION VALIDATION ---
        if (hasRxItem) {
            if (request.getPrescriptionId() == null) {
                throw new BadRequestException("Prescription is required for these items.");
            }

            // Interceptor handles the token/auth now!
            PrescriptionClientResponse rxDetails = catalogClient.getPrescriptionById(request.getPrescriptionId());

            if (rxDetails == null || !"APPROVED".equalsIgnoreCase(rxDetails.getStatus())) {
                String status = (rxDetails != null) ? rxDetails.getStatus() : "NOT_FOUND";
                throw new BadRequestException("Prescription not approved. Status: " + status);
            }

            if (!userId.equals(rxDetails.getUserId())) {
                throw new BadRequestException("Prescription mismatch: This Rx belongs to another user.");
            }
        }

        // --- BUILD ORDER ---
        Order order = new Order();
        order.setUserId(userId);
        order.setOrderNumber(generateOrderNumber());
        order.setDeliverySlot(request.getDeliverySlot());
        order.setAddressId(address.getId());
        order.setDeliveryAddress(OrderMapper.buildAddressSnapshot(address));
        order.setPrescriptionId(request.getPrescriptionId());

        double subTotal = 0.0;
        for (CartItem ci : cartItems) {
            OrderItem oi = new OrderItem(order, ci.getMedicineId(), ci.getMedicineName(), ci.getQuantity(), ci.getPrice());
            oi.setRequiresPrescription(ci.getRequiresPrescription());
            order.getItems().add(oi);
            subTotal += ci.getPrice() * ci.getQuantity();
        }

        double deliveryCharge = subTotal < FREE_DELIVERY_ABOVE ? DELIVERY_CHARGE : 0.0;
        order.setSubTotal(subTotal);
        order.setDeliveryCharge(deliveryCharge);
        order.setDiscountAmount(0.0);
        order.setTotalAmount(subTotal + deliveryCharge);
        order.setStatus(hasRxItem && request.getPrescriptionId() == null ? 
                        OrderStatus.PRESCRIPTION_PENDING : OrderStatus.PAYMENT_PENDING);

        Order saved = orderRepository.save(order);
        cartService.clearCart(userId);

        // --- ASYNC NOTIFICATION BLOCK ---
        try {
            // Get user details and notify
            UserResponse user = userClient.getUserById(userId);
            orderEventPublisher.publishOrderPlaced(order, user.getName(), user.getEmail());
        } catch (Exception e) {
            // Log but don't crash. Order is already saved.
            System.err.println("WARN: Order placed but user notification failed: " + e.getMessage());
        }

        return OrderMapper.toOrderResponse(saved);
    }

    @Override
    public OrderResponse getOrderById(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return OrderMapper.toOrderResponse(order);
    }

    @Override
    public Page<OrderResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(OrderMapper::toOrderResponse);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId, String reason) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        Set<OrderStatus> cancellableStatuses = Set.of(
                OrderStatus.CHECKOUT_STARTED, OrderStatus.PRESCRIPTION_PENDING,
                OrderStatus.PRESCRIPTION_APPROVED, OrderStatus.PAYMENT_PENDING
        );

        if (!cancellableStatuses.contains(order.getStatus())) {
            throw new InvalidOrderStateException("Order cannot be cancelled in status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CUSTOMER_CANCELLED);
        order.setCancellationReason(reason);
        return OrderMapper.toOrderResponse(orderRepository.save(order));
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        validateTransition(order.getStatus(), newStatus);
        order.setStatus(newStatus);
        return OrderMapper.toOrderResponse(orderRepository.save(order));
    }

    @Override
    public Page<OrderResponse> getAllOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable)
                .map(OrderMapper::toOrderResponse);
    }

    private void validateTransition(OrderStatus current, OrderStatus next) {
        Set<OrderStatus> allowed = VALID_TRANSITIONS.get(current);
        if (allowed == null || !allowed.contains(next)) {
            throw new InvalidOrderStateException("Invalid transition: " + current + " → " + next);
        }
    }

    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int random = (int) (Math.random() * 9000) + 1000;
        return "ORD-" + timestamp + "-" + random;
    }
}