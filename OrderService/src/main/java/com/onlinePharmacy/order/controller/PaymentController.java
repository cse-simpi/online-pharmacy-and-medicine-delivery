package com.onlinePharmacy.order.controller;


import com.onlinePharmacy.order.dto.PaymentRequest;
import com.onlinePharmacy.order.dto.PaymentResponse;
import com.onlinePharmacy.order.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/payments")
@Tag(name = "Payments", description = "Payment initiation and status")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment for an order (stub gateway)")
    public ResponseEntity<PaymentResponse> initiatePayment(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long orderId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.initiatePayment(userId, orderId, request));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment details for an order")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getPaymentByOrderId(orderId));
    }
}