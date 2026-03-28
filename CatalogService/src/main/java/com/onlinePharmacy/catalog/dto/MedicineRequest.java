package com.onlinePharmacy.catalog.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public class MedicineRequest {

	@NotBlank(message = "Medicine name is required")
	private String name;
	
	private String description;
	
	@NotBlank(message = "Manufacturer is required")
	private String manufacturer;
	
	@NotNull(message = "Price is required")
	@Positive(message = "Pricem must be greater than 0")
	private Double price;
	
	@PositiveOrZero(message = "Discounted price must be >= 0")
	private Double discountedPrice;
	
	@NotNull(message = "Stock is required")
	@PositiveOrZero(message = "Stock must be greater than or equal to 0")
	private Integer stock;
	
	@NotNull(message = "generic name is required")
	private String genericName;
	
	private Boolean requiresPrescription = false;
	
	@Future(message = "Expiry date must be in the future")
	private LocalDate expiryDate;
	
	private String dosageNotes;
	
	@NotNull(message = "Category Id is required")
	private Long categoryId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(Double discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public Boolean getRequiresPrescription() {
		return requiresPrescription;
	}

	public void setRequiresPrescription(Boolean requiresPrescription) {
		this.requiresPrescription = requiresPrescription;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
	}


	public String getDosageNotes() {
		return dosageNotes;
	}

	public void setDosageNotes(String dosageNotes) {
		this.dosageNotes = dosageNotes;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getGenericName() {
		return genericName;
	}

	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	
	
	
	
}
