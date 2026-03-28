package com.onlinePharmacy.order.dto;

import java.util.List;

public class CartResponse {
	private List<CartItemResponse> items;
    private Double subTotal;
    private Double deliveryCharge;
    private Double totalAmount;
    private int itemCount;
    private boolean hasPrescriptionItems;
    
	public CartResponse() {
		
	}

	public List<CartItemResponse> getItems() {
		return items;
	}

	public void setItems(List<CartItemResponse> items) {
		this.items = items;
	}

	public Double getSubTotal() {
		return subTotal;
	}

	public void setSubTotal(Double subTotal) {
		this.subTotal = subTotal;
	}

	public Double getDeliveryCharge() {
		return deliveryCharge;
	}

	public void setDeliveryCharge(Double deliveryCharge) {
		this.deliveryCharge = deliveryCharge;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getItemCount() {
		return itemCount;
	}

	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	public boolean isHasPrescriptionItems() {
		return hasPrescriptionItems;
	}

	public void setHasPrescriptionItems(boolean hasPrescriptionItems) {
		this.hasPrescriptionItems = hasPrescriptionItems;
	}
    
    
}
