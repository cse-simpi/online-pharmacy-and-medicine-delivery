package com.onlinePharmacy.admin.controller;

import com.onlinePharmacy.admin.dto.OrderDto;
import com.onlinePharmacy.admin.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PRD Section 13: Admin Orders Management
 * Exposes:
 *   GET   /api/admin/orders              — list all orders filtered by status (paginated)
 *   GET   /api/admin/orders/{id}         — get single order detail
 *   PATCH /api/admin/orders/{id}/status  — update status:
 *                                           Packed / Out for Delivery / Delivered / Cancelled
 *
 * All order data is fetched from order-service via OpenFeign.
 * Status changes are recorded in admin_audit_logs table.
 * The gateway already enforces ADMIN role on /api/admin/**
 * but @PreAuthorize here gives defence-in-depth per PRD security requirements.
 */
@RestController
@RequestMapping("/api/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Orders", description = "PRD §13 — Order lifecycle management")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    @GetMapping
    @Operation(summary = "List all orders filtered by status",
               description = "Valid statuses: PAID, PACKED, OUT_FOR_DELIVERY, DELIVERED, CUSTOMER_CANCELLED, ADMIN_CANCELLED")
    public ResponseEntity<Map<String, Object>> getAllOrders(
            @RequestParam(defaultValue = "PAID") String status,
            @RequestParam(defaultValue = "0")    int    page,
            @RequestParam(defaultValue = "10")   int    size) {
        return ResponseEntity.ok(adminOrderService.getAllOrders(status, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get full order details by ID")
    public ResponseEntity<OrderDto> getOrderById(
            @PathVariable                   Long   id,
            @RequestHeader("X-User-Id")     String adminId) {
        return ResponseEntity.ok(adminOrderService.getOrderById(id, adminId));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update order status — Packed / Out for Delivery / Delivered / Admin Cancelled",
               description = "Allowed transitions per PRD: PAID→PACKED, PACKED→OUT_FOR_DELIVERY, OUT_FOR_DELIVERY→DELIVERED, PAID→ADMIN_CANCELLED")
    public ResponseEntity<OrderDto> updateOrderStatus(
            @PathVariable                   Long   id,
            @RequestParam                   String status,
            @RequestHeader("X-User-Id")     Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        return ResponseEntity.ok(
                adminOrderService.updateOrderStatus(id, status,adminId, adminEmail));
    }
}