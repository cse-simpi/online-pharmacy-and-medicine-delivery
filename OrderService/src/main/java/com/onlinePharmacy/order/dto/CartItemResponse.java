package com.onlinePharmacy.order.dto;

public class CartItemResponse {
	 private Long id;
	 private Long medicineId;
	 private String medicineName;
	 private Double price;
	 private Integer quantity;
	 private Double lineTotal;
	 private Boolean requiresPrescription;
	 private Long prescriptionId;
	 private Boolean substitutionAllowed;
	 private String genericName;
	 
	 public CartItemResponse() {
		
	 }


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


	 public Double getPrice() {
		 return price;
	 }


	 public void setPrice(Double price) {
		 this.price = price;
	 }


	 public Integer getQuantity() {
		 return quantity;
	 }


	 public void setQuantity(Integer quantity) {
		 this.quantity = quantity;
	 }


	 public Double getLineTotal() {
		 return lineTotal;
	 }


	 public void setLineTotal(Double lineTotal) {
		 this.lineTotal = lineTotal;
	 }


	 public Boolean getRequiresPrescription() {
		 return requiresPrescription;
	 }


	 public void setRequiresPrescription(Boolean requiresPrescription) {
		 this.requiresPrescription = requiresPrescription;
	 }


	 public Long getPrescriptionId() {
		 return prescriptionId;
	 }


	 public void setPrescriptionId(Long prescriptionId) {
		 this.prescriptionId = prescriptionId;
	 }


	 public Boolean getSubstitutionAllowed() {
		 return substitutionAllowed;
	 }


	 public void setSubstitutionAllowed(Boolean substitutionAllowed) {
		 this.substitutionAllowed = substitutionAllowed;
	 }


	 public String getGenericName() {
		 return genericName;
	 }


	 public void setGenericName(String genericName) {
		 this.genericName = genericName;
	 }
	 
	 
}
