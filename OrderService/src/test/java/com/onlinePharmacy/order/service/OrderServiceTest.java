package com.onlinePharmacy.order.service;


import com.onlinePharmacy.order.dto.CheckoutRequest;
import com.onlinePharmacy.order.dto.OrderResponse;
import com.onlinePharmacy.order.entity.*;
import com.onlinePharmacy.order.entity.OrderStatus;
import com.onlinePharmacy.order.exception.BadRequestException;
import com.onlinePharmacy.order.exception.InvalidOrderStateException;
import com.onlinePharmacy.order.exception.ResourceNotFoundException;
import com.onlinePharmacy.order.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository     orderRepository;
    @Mock private CartItemRepository  cartItemRepository;
    @Mock private AddressRepository   addressRepository;
    @Mock private CartService         cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private CartItem   cartItem;
    private Address    address;
    private Order      order;
    private CheckoutRequest checkoutRequest;

    @BeforeEach
    void setUp() {
        cartItem = new CartItem();
        cartItem.setUserId(1L);
        cartItem.setMedicineId(10L);
        cartItem.setMedicineName("Paracetamol");
        cartItem.setPrice(200.0);
        cartItem.setQuantity(2);
        cartItem.setRequiresPrescription(false);

        address = new Address();
        address.setUserId(1L);
        address.setFullName("John Doe");
        address.setPhone("9876543210");
        address.setAddressLine1("123 Main St");
        address.setCity("Mumbai");
        address.setState("Maharashtra");
        address.setPincode("400001");
        address.setCountry("India");

        order = new Order();
        order.setUserId(1L);
        order.setOrderNumber("ORD-20240101-1234");
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setSubTotal(400.0);
        order.setDeliveryCharge(49.0);
        order.setTotalAmount(449.0);

        checkoutRequest = new CheckoutRequest();
        checkoutRequest.setAddressId(1L);
        checkoutRequest.setDeliverySlot("10AM-12PM");
    }

    // ── Checkout Tests ────────────────────────────────────────────────────────

    @Test
    void startCheckout_emptyCart_throwsBadRequest() {
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> orderService.startCheckout(1L, checkoutRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Cart is empty");
    }

    @Test
    void startCheckout_addressNotFound_throwsResourceNotFound() {
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.startCheckout(1L, checkoutRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Address not found");
    }

    @Test
    void startCheckout_normalItems_createsOrderInPaymentPending() {
        when(cartItemRepository.findByUserId(1L)).thenReturn(List.of(cartItem));
        when(addressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(address));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = orderService.startCheckout(1L, checkoutRequest);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING.name());
        verify(cartService, times(1)).clearCart(1L);
    }

    

    // ── Get Order Tests ───────────────────────────────────────────────────────

    @Test
    void getOrderById_success() {
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L, 1L);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PAYMENT_PENDING.name());
    }

    @Test
    void getOrderById_notFound_throwsException() {
        when(orderRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L, 99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── Cancel Order Tests ────────────────────────────────────────────────────

    @Test
    void cancelOrder_inPaymentPending_succeeds() {
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponse response = orderService.cancelOrder(1L, 1L, "Changed mind");

        assertThat(response.getStatus()).isEqualTo(OrderStatus.CUSTOMER_CANCELLED.name());
    }

    @Test
    void cancelOrder_afterPacked_throwsInvalidState() {
        order.setStatus(OrderStatus.PACKED);
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 1L, "Too late"))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("cannot be cancelled");
    }

    // ── Status Transition Tests ───────────────────────────────────────────────

    @Test
    void updateOrderStatus_validTransition_succeeds() {
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponse response = orderService.updateOrderStatus(1L, OrderStatus.PACKED);

        assertThat(response.getStatus()).isEqualTo(OrderStatus.PACKED.name());
    }

    @Test
    void updateOrderStatus_invalidTransition_throwsException() {
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.updateOrderStatus(1L, OrderStatus.PACKED))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Invalid transition");
    }
}