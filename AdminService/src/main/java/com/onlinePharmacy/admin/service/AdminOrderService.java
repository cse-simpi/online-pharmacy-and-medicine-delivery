package com.onlinePharmacy.admin.service;


import org.springframework.stereotype.Service;

import com.onlinePharmacy.admin.client.OrderClient;
import com.onlinePharmacy.admin.dto.OrderDto;
import com.onlinePharmacy.admin.entity.AdminAuditLog.AdminAction;
import com.onlinePharmacy.admin.exception.ServiceUnavailableException;

import java.util.Map;

@Service
public class AdminOrderService {

    private final OrderClient orderClient;
    private final AuditLogService auditLogService;

    public AdminOrderService(OrderClient orderClient, AuditLogService auditLogService) {
        this.orderClient     = orderClient;
        this.auditLogService = auditLogService;
    }

    public Map<String, Object> getAllOrders(String status, int page, int size) {
        Map<String, Object> result = orderClient.getAllOrders(status, page, size);
        if (result == null) throw new ServiceUnavailableException("Order service unavailable.");
        return result;
    }

    public OrderDto getOrderById(Long orderId, String adminId) {
        OrderDto order = orderClient.getOrderById(orderId, adminId);
        if (order == null) throw new ServiceUnavailableException("Order service unavailable.");
        return order;
    }

    public OrderDto updateOrderStatus(Long orderId, String newStatus,
                                      Long adminId, String adminEmail) {
        OrderDto updated = orderClient.updateOrderStatus(orderId, newStatus);
        if (updated == null) throw new ServiceUnavailableException("Order service unavailable.");

        // ✅ Persist audit log in admin DB
        auditLogService.log(
                adminId, adminEmail,
                AdminAction.ORDER_STATUS_UPDATED,
                "ORDER", orderId,
                "Order #" + orderId + " status changed to " + newStatus
        );

        return updated;
    }
}