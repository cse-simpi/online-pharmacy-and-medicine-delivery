package com.onlinePharmacy.order.controller;


import com.onlinePharmacy.order.dto.CheckoutRequest;
import com.onlinePharmacy.order.dto.OrderResponse;
import com.onlinePharmacy.order.entity.OrderStatus;
import com.onlinePharmacy.order.messaging.OrderEventPublisher;
import com.onlinePharmacy.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Order checkout and history")
public class OrderController {

    private final OrderService orderService;
    
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
        
    }

    // ── Customer Endpoints ────────────────────────────────────────────────────

    @PostMapping("/checkout/start")
    @Operation(summary = "Start checkout — validates prescription via catalog-service if Rx items present")
    public ResponseEntity<OrderResponse> startCheckout(
            @RequestHeader("X-User-Id")     Long userId,
            @RequestHeader("Authorization" ) String bearerToken,
             @RequestBody CheckoutRequest request) {
    	
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.startCheckout(userId, request));
    }

    @GetMapping
    @Operation(summary = "Get current user's order history")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
    	System.out.println("Get orders");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getUserOrders(userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific order by ID")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(userId, id));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order (customer)")
    public ResponseEntity<OrderResponse> cancelOrder(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body) {
        String reason = body != null ? body.get("reason") : null;
        return ResponseEntity.ok(orderService.cancelOrder(userId, id, reason));
    }

    // ── Admin Endpoints ───────────────────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all orders by status (Admin only)")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(
            @RequestParam(defaultValue = "PAID") OrderStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(orderService.getAllOrdersByStatus(status, pageable));
    }

    @PatchMapping("/admin/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order status (Admin only)")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}
