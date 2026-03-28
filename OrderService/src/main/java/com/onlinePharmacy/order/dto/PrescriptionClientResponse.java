package com.onlinePharmacy.order.dto;

import java.time.LocalDateTime;

/**
 * Mirror of catalog-service's PrescriptionResponse.
 * Only fields the order-service needs for validation.
 */
public class PrescriptionClientResponse {

    private Long   id;
    private Long   userId;
    private Long   medicineId;
    private String status;   // PENDING | APPROVED | REJECTED
    private String fileName;
    private LocalDateTime createdAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getMedicineId() { return medicineId; }
    public void setMedicineId(Long medicineId) { this.medicineId = medicineId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
