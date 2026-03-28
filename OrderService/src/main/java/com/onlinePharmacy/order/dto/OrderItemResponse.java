package com.onlinePharmacy.order.dto;

public class OrderItemResponse {
	private Long id;
    private Long medicineId;
    private String medicineName;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
    private Boolean requiresPrescription;
    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getMedicineId() {
		return medicineId;
	}
	public void setMedicineId(Long medicineId) {
		this.medicineId = medicineId;
	}
	public String getMedicineName() {
		return medicineName;
	}
	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Boolean getRequiresPrescription() {
		return requiresPrescription;
	}
	public void setRequiresPrescription(Boolean requiresPrescription) {
		this.requiresPrescription = requiresPrescription;
	}
    
    
}
