package com.onlinePharmacy.order.util;


import com.onlinePharmacy.order.dto.*;
import com.onlinePharmacy.order.entity.*;

import java.util.stream.Collectors;

public class OrderMapper {

    private OrderMapper() {}

    public static CartItemResponse toCartItemResponse(CartItem c) {
        CartItemResponse dto = new CartItemResponse();
        dto.setId(c.getId());
        dto.setMedicineId(c.getMedicineId());
        dto.setMedicineName(c.getMedicineName());
        dto.setPrice(c.getPrice());
        dto.setQuantity(c.getQuantity());
        dto.setLineTotal(c.getPrice() * c.getQuantity());
        dto.setRequiresPrescription(c.getRequiresPrescription());
        dto.setPrescriptionId(c.getPrescriptionId());
        dto.setSubstitutionAllowed(c.getSubstitutionAllowed());
        dto.setGenericName(c.getGenericName());
        return dto;
    }

    public static OrderItemResponse toOrderItemResponse(OrderItem i) {
        OrderItemResponse dto = new OrderItemResponse();
        dto.setId(i.getId());
        dto.setMedicineId(i.getMedicineId());
        dto.setMedicineName(i.getMedicineName());
        dto.setQuantity(i.getQuantity());
        dto.setUnitPrice(i.getUnitPrice());
        dto.setTotalPrice(i.getTotalPrice());
        dto.setRequiresPrescription(i.getRequiresPrescription());
        return dto;
    }

    public static PaymentResponse toPaymentResponse(Payment p) {
        if (p == null) return null;
        PaymentResponse dto = new PaymentResponse();
        dto.setId(p.getId());
        dto.setOrderId(p.getOrder() != null ? p.getOrder().getId() : null);
        dto.setTransactionId(p.getTransactionId());
        dto.setPaymentMethod(p.getPaymentMethod());
        dto.setAmount(p.getAmount());
        dto.setStatus(p.getStatus().name());
        dto.setPaidAt(p.getPaidAt());
        dto.setCreatedAt(p.getCreatedAt());
        dto.setUpdatedAt(p.getCreatedAt());
        return dto;
    }

    public static OrderResponse toOrderResponse(Order o) {
        OrderResponse dto = new OrderResponse();
        dto.setId(o.getId());
        dto.setOrderNumber(o.getOrderNumber());
        dto.setUserId(o.getUserId());
        dto.setStatus(o.getStatus().name());
        dto.setSubTotal(o.getSubTotal());
        dto.setDiscountAmount(o.getDiscountAmount());
        dto.setDeliveryCharge(o.getDeliveryCharge());
        dto.setTotalAmount(o.getTotalAmount());
        dto.setDeliverySlot(o.getDeliverySlot());
        dto.setDeliveryAddress(o.getDeliveryAddress());
        dto.setPrescriptionId(o.getPrescriptionId());
        dto.setCreatedAt(o.getCreatedAt());
        dto.setUpdatedAt(o.getUpdatedAt());
        
        
        if (o.getItems() != null) {
            dto.setItems(o.getItems().stream()
                    .map(OrderMapper::toOrderItemResponse)
                    .collect(Collectors.toList()));
        }
        dto.setPayment(toPaymentResponse(o.getPayment()));
        return dto;
    }

    public static AddressResponse toAddressResponse(Address a) {
        AddressResponse dto = new AddressResponse();
        dto.setId(a.getId());
        dto.setUserId(a.getUserId());
        dto.setFullName(a.getFullName());
        dto.setPhone(a.getPhone());
        dto.setAddressLine1(a.getAddressLine1());
        dto.setAddressLine2(a.getAddressLine2());
        dto.setCity(a.getCity());
        dto.setState(a.getState());
        dto.setPincode(a.getPincode());
        dto.setCountry(a.getCountry());
        dto.setIsDefault(a.getIsDefault());
        dto.setCreatedAt(a.getCreatedAt());
        dto.setUpdatedAt(a.getUpdatedAt());
        return dto;
    }

    public static String buildAddressSnapshot(Address a) {
        StringBuilder sb = new StringBuilder();
        sb.append(a.getFullName()).append(", ");
        sb.append(a.getAddressLine1());
        if (a.getAddressLine2() != null && !a.getAddressLine2().isBlank()) {
            sb.append(", ").append(a.getAddressLine2());
        }
        sb.append(", ").append(a.getCity());
        sb.append(", ").append(a.getState());
        sb.append(" - ").append(a.getPincode());
        sb.append(", ").append(a.getCountry());
        sb.append(" | Ph: ").append(a.getPhone());
        return sb.toString();
    }
}