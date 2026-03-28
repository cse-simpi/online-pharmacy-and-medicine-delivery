package com.onlinePharmacy.order.service;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.onlinePharmacy.order.dto.PaymentRequest;
import com.onlinePharmacy.order.dto.PaymentResponse;
import com.onlinePharmacy.order.entity.Order;
import com.onlinePharmacy.order.entity.OrderStatus;
import com.onlinePharmacy.order.entity.Payment;
import com.onlinePharmacy.order.entity.PaymentStatus;
import com.onlinePharmacy.order.exception.BadRequestException;
import com.onlinePharmacy.order.exception.ResourceNotFoundException;
import com.onlinePharmacy.order.repository.OrderRepository;
import com.onlinePharmacy.order.repository.PaymentRepository;
import com.onlinePharmacy.order.util.OrderMapper;

import java.time.LocalDateTime;

import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository   orderRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository   = orderRepository;
    }

    @Override
    @Transactional
    public PaymentResponse initiatePayment(Long userId, Long orderId, PaymentRequest request) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // Payment only allowed in PAYMENT_PENDING state
        if (order.getStatus() != OrderStatus.PAYMENT_PENDING) {
            throw new BadRequestException(
                    "Payment cannot be initiated for order in status: " + order.getStatus());
        }

        // Prevent double-payment
        paymentRepository.findByOrderId(orderId).ifPresent(existing -> {
            if (existing.getStatus() == PaymentStatus.SUCCESS) {
                throw new BadRequestException("Payment already completed for this order.");
            }
        });

        // ── Stub Payment Gateway ──────────────────────────────────────────────
        // In production: call Razorpay / Paytm / Stripe SDK here
        // For now: simulate success for all non-COD, always succeed
        boolean paymentSuccess = simulatePaymentGateway(request.getPaymentMethod());
        // ─────────────────────────────────────────────────────────────────────

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionId(generateTransactionId());
        payment.setGatewayResponse("{\"stub\":true,\"method\":\"" + request.getPaymentMethod() + "\"}");

        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setPaidAt(LocalDateTime.now());
            order.setStatus(OrderStatus.PAID);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.PAYMENT_FAILED);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);

        return OrderMapper.toPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No payment found for order: " + orderId));
        return OrderMapper.toPaymentResponse(payment);
    }

    // ── Stub Gateway Simulation ───────────────────────────────────────────────

    private boolean simulatePaymentGateway(String method) {
        // COD always succeeds immediately in stub
        // UPI/CARD/NETBANKING: 90% success simulation
        if ("COD".equalsIgnoreCase(method)) return true;
        return Math.random() > 0.1;
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }
}