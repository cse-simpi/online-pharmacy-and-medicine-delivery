package com.onlinePharmacy.catalog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "prescriptions")
public class Prescription {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "medicine_id")
	private Long medicineId;
	
	@Column(name = "file_name", nullable = false)
	private String fileName;
	
	@Column(name = "file_path", nullable = false)
	private String filePath;
	
	@Column(name = "file_type", nullable = false)
	private String fileType;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PrescriptionStatus status;
	
	
	@Column(name = "rejection_reason")
	private String rejectionReason;
	
	@Column(name = "verified_by")
	private Long verifiedBy;
	
	@PrePersist
	protected void onCreate() {
		if(this.status == null) {
			this.status = PrescriptionStatus.PENDING;
		}
	}

	public Prescription() {
		
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getMedicineId() {
		return medicineId;
	}

	public void setMedicineId(Long medicineId) {
		this.medicineId = medicineId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public PrescriptionStatus getStatus() {
		return status;
	}

	public void setStatus(PrescriptionStatus status) {
		this.status = status;
	}

	public String getRejectionReason() {
		return rejectionReason;
	}

	public void setRejectionReason(String rejectionReason) {
		this.rejectionReason = rejectionReason;
	}

	public Long getVerifiedBy() {
		return verifiedBy;
	}

	public void setVerifiedBy(Long verifiedBy) {
		this.verifiedBy = verifiedBy;
	}
	
	
	
	
}
