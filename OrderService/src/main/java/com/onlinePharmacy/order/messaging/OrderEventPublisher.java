package com.onlinePharmacy.order.messaging;

import com.onlinePharmacy.order.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "pharmacy.events";
    private static final String ROUTING_KEY = "order.placed";

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // 1. Change the signature to accept email and name
    public void publishOrderPlaced(Order order, String userEmail, String userName) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("orderId", order.getId());
            event.put("orderNumber", order.getOrderNumber());
            event.put("userId", order.getUserId());
            event.put("totalAmount", order.getTotalAmount());
            event.put("status", order.getStatus().name());
            event.put("hasPrescriptionItems", order.getItems().stream().anyMatch(i -> i.getRequiresPrescription()));
            event.put("placedAt", order.getCreatedAt().toString());
            event.put("deliveryAddress", order.getDeliveryAddress());
            event.put("deliverySlot", order.getDeliverySlot());
            
            // 2. Use the passed arguments instead of hardcoded strings
            event.put("userEmail", userEmail); 
            event.put("userName", userName);

            List<Map<String, Object>> items = order.getItems().stream().map(i -> {
                Map<String, Object> item = new HashMap<>();
                item.put("medicineName", i.getMedicineName());
                item.put("quantity", i.getQuantity());
                item.put("unitPrice", i.getUnitPrice());
                item.put("totalPrice", i.getTotalPrice());
                return item;
            }).collect(Collectors.toList());
            
            event.put("items", items);

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
            log.info("Published ORDER_PLACED event for order: {} to email: {}", order.getOrderNumber(), userEmail);
            
        } catch (Exception e) {
            log.error("Failed to publish order event: {}", e.getMessage());
        }
    }
}