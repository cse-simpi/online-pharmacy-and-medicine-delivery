package com.onlinePharmacy.admin.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// ── Medicine ──────────────────────────────────────────────────────────────────
public class MedicineDto {
    private Long      id;
    private String    name;
    private String    description;
    private String    manufacturer;
    private Double    price;
    private Double    discountedPrice;
    private Integer   stock;
    private Boolean   requiresPrescription;
    private LocalDate expiryDate;
    private String    imageUrl;
    private String    dosageNotes;
    private Boolean   active;
    private Long      categoryId;
    private String    categoryName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getDiscountedPrice() { return discountedPrice; }
    public void setDiscountedPrice(Double discountedPrice) { this.discountedPrice = discountedPrice; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public Boolean getRequiresPrescription() { return requiresPrescription; }
    public void setRequiresPrescription(Boolean requiresPrescription) { this.requiresPrescription = requiresPrescription; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDosageNotes() { return dosageNotes; }
    public void setDosageNotes(String dosageNotes) { this.dosageNotes = dosageNotes; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
}