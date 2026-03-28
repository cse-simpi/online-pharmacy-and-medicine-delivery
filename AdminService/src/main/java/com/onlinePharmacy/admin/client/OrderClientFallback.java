package com.onlinePharmacy.admin.client;

import com.onlinePharmacy.admin.dto.OrderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;

@Component
public class OrderClientFallback implements OrderClient {

    private static final Logger log = LoggerFactory.getLogger(OrderClientFallback.class);

    @Override
    public Map<String, Object> getAllOrders(String status, int page, int size) {
        log.error("OrderClient fallback: getAllOrders");
        return Map.of("content", Collections.emptyList(), "totalElements", 0);
    }

    @Override
    public OrderDto getOrderById(Long id, String adminId) {
        log.error("OrderClient fallback: getOrderById({})", id);
        return null;
    }

    @Override
    public OrderDto updateOrderStatus(Long id, String status) {
        log.error("OrderClient fallback: updateOrderStatus({})", id);
        return null;
    }
}