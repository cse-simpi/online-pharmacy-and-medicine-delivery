package com.onlinePharmacy.notification.messaging.event;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Mirrors the payload published by order-service's OrderEventPublisher.
 * Fields must match the map keys set in publishOrderPlaced().
 */
public class OrderPlacedEvent {

    private Long          orderId;
    private String        orderNumber;
    private Long          userId;
    private Double        totalAmount;
    private String        status;
    private boolean       hasPrescriptionItems;
    private String        placedAt;

    // Additional fields we expect from the enriched event
    private String        userEmail;      // added to publisher in order-service
    private String        userName;       // added to publisher in order-service
    private String        deliveryAddress;
    private String        deliverySlot;
    private List<OrderItemInfo> items;

    public static class OrderItemInfo {
        private String  medicineName;
        private Integer quantity;
        private Double  unitPrice;
        private Double  totalPrice;

        public String getMedicineName() { return medicineName; }
        public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public Double getUnitPrice() { return unitPrice; }
        public void setUnitPrice(Double unitPrice) { this.unitPrice = unitPrice; }
        public Double getTotalPrice() { return totalPrice; }
        public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }
    }

    public OrderPlacedEvent() {}

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public boolean isHasPrescriptionItems() { return hasPrescriptionItems; }
    public void setHasPrescriptionItems(boolean hasPrescriptionItems) { this.hasPrescriptionItems = hasPrescriptionItems; }
    public String getPlacedAt() { return placedAt; }
    public void setPlacedAt(String placedAt) { this.placedAt = placedAt; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public String getDeliverySlot() { return deliverySlot; }
    public void setDeliverySlot(String deliverySlot) { this.deliverySlot = deliverySlot; }
    public List<OrderItemInfo> getItems() { return items; }
    public void setItems(List<OrderItemInfo> items) { this.items = items; }
}