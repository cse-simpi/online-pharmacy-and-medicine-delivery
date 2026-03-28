package com.onlinePharmacy.admin.client;

import com.onlinePharmacy.admin.dto.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
    name     = "order-service",
    fallback = OrderClientFallback.class
)
public interface OrderClient {

    @GetMapping("/api/orders/admin/all")
    Map<String, Object> getAllOrders(
            @RequestParam(defaultValue = "PAID") String status,
            @RequestParam(defaultValue = "0")    int page,
            @RequestParam(defaultValue = "10")   int size);

    @GetMapping("/api/orders/{id}")
    OrderDto getOrderById(
            @PathVariable("id") Long id,
            @RequestHeader("X-User-Id")     String adminId);

    @PatchMapping("/api/orders/admin/{id}/status")
    OrderDto updateOrderStatus(
            @PathVariable("id") Long id,
            @RequestParam String status);
}