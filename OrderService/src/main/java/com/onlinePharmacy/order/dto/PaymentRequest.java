package com.onlinePharmacy.order.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentRequest {
	@NotBlank(message = "Payment method is required")
    private String paymentMethod; // UPI, CARD, COD, NETBANKING
 
    private String upiId;
    private String cardToken;
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getUpiId() {
		return upiId;
	}
	public void setUpiId(String upiId) {
		this.upiId = upiId;
	}
	public String getCardToken() {
		return cardToken;
	}
	public void setCardToken(String cardToken) {
		this.cardToken = cardToken;
	}
    
    
}
