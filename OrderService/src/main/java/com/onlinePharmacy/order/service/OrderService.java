package com.onlinePharmacy.order.service;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.onlinePharmacy.order.dto.CheckoutRequest;
import com.onlinePharmacy.order.dto.OrderResponse;
import com.onlinePharmacy.order.entity.OrderStatus;

public interface OrderService {
    OrderResponse startCheckout(Long userId, CheckoutRequest request);
    OrderResponse getOrderById(Long userId, Long orderId);
    Page<OrderResponse> getUserOrders(Long userId, Pageable pageable);
    OrderResponse cancelOrder(Long userId, Long orderId, String reason);
    OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus);
    Page<OrderResponse> getAllOrdersByStatus(OrderStatus status, Pageable pageable);
}
