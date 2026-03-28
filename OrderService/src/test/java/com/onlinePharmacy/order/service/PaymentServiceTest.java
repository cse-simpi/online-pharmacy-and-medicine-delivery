package com.onlinePharmacy.order.service;


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
import com.onlinePharmacy.order.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private OrderRepository   orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setUserId(1L);
        order.setOrderNumber("ORD-TEST-001");
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setTotalAmount(500.0);

        paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentMethod("COD");
    }

    @Test
    void initiatePayment_orderNotFound_throwsException() {
        when(orderRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, 99L, paymentRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Order not found");
    }

    @Test
    void initiatePayment_orderNotInPaymentPending_throwsBadRequest() {
        order.setStatus(OrderStatus.PAID);
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, 1L, paymentRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Payment cannot be initiated");
    }

    @Test
    void initiatePayment_cod_alwaysSucceeds() {
        paymentRequest.setPaymentMethod("COD");
        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> {
            Payment p = inv.getArgument(0);
            p.setOrder(order);
            return p;
        });
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        PaymentResponse response = paymentService.initiatePayment(1L, 1L, paymentRequest);

        // COD always succeeds in stub
        assertThat(response.getStatus()).isEqualTo(PaymentStatus.SUCCESS.name());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    @Test
    void initiatePayment_alreadyPaid_throwsBadRequest() {
        Payment existingPayment = new Payment();
        existingPayment.setStatus(PaymentStatus.SUCCESS);

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(existingPayment));

        assertThatThrownBy(() -> paymentService.initiatePayment(1L, 1L, paymentRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already completed");
    }

    @Test
    void getPaymentByOrderId_notFound_throwsException() {
        when(paymentRepository.findByOrderId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> paymentService.getPaymentByOrderId(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("No payment found");
    }

    @Test
    void getPaymentByOrderId_found_returnsResponse() {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(500.0);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setPaymentMethod("COD");
        payment.setTransactionId("TXN-ABC123");

        when(paymentRepository.findByOrderId(1L)).thenReturn(Optional.of(payment));

        PaymentResponse response = paymentService.getPaymentByOrderId(1L);

        assertThat(response.getStatus()).isEqualTo("SUCCESS");
        assertThat(response.getAmount()).isEqualTo(500.0);
        assertThat(response.getPaymentMethod()).isEqualTo("COD");
    }
}