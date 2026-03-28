package com.onlinePharmacy.order.service;


import com.onlinePharmacy.order.dto.PaymentRequest;
import com.onlinePharmacy.order.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse initiatePayment(Long userId, Long orderId, PaymentRequest request);
    PaymentResponse getPaymentByOrderId(Long orderId);
}