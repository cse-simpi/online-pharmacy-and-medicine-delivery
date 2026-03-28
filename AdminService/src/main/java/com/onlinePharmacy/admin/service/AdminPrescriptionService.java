package com.onlinePharmacy.admin.service;

import org.springframework.stereotype.Service;

import com.onlinePharmacy.admin.client.CatalogClient;
import com.onlinePharmacy.admin.dto.PrescriptionDto;
import com.onlinePharmacy.admin.entity.AdminAuditLog.AdminAction;
import com.onlinePharmacy.admin.exception.ServiceUnavailableException;

import java.util.Map;

@Service
public class AdminPrescriptionService {

    private final CatalogClient   catalogClient;
    private final AuditLogService auditLogService;

    public AdminPrescriptionService(CatalogClient catalogClient,
                                    AuditLogService auditLogService) {
        this.catalogClient   = catalogClient;
        this.auditLogService = auditLogService;
    }

    /**
     * GET /api/admin/prescriptions
     * Returns paginated list from catalog-service filtered by status.
     */
    public Map<String, Object> getPrescriptions(String status, int page, int size) {
        Map<String, Object> result = catalogClient.getPrescriptionsByStatus(status, page, size);
        if (result == null) throw new ServiceUnavailableException("Catalog service unavailable.");
        return result;
    }

    /**
     * PATCH /api/admin/prescriptions/{id}/approve
     * Approves a prescription and records audit.
     */
    public PrescriptionDto approvePrescription(Long prescriptionId,
                                               Long adminId, String adminEmail) {
        PrescriptionDto result = catalogClient.updatePrescriptionStatus(
                prescriptionId, "APPROVED", null, adminId.toString());
        if (result == null) throw new ServiceUnavailableException("Catalog service unavailable.");

        auditLogService.log(
                adminId, adminEmail,
                AdminAction.PRESCRIPTION_APPROVED,
                "PRESCRIPTION", prescriptionId,
                "Prescription #" + prescriptionId + " approved by admin " + adminEmail
        );
        return result;
    }

    /**
     * PATCH /api/admin/prescriptions/{id}/reject
     * Rejects a prescription with a reason and records audit.
     */
    public PrescriptionDto rejectPrescription(Long prescriptionId, String rejectionReason,
                                               Long adminId, String adminEmail) {
        PrescriptionDto result = catalogClient.updatePrescriptionStatus(
                prescriptionId, "REJECTED", rejectionReason, adminId.toString());
        if (result == null) throw new ServiceUnavailableException("Catalog service unavailable.");

        auditLogService.log(
                adminId, adminEmail,
                AdminAction.PRESCRIPTION_REJECTED,
                "PRESCRIPTION", prescriptionId,
                "Prescription #" + prescriptionId + " rejected. Reason: " + rejectionReason
        );
        return result;
    }

    /**
     * GET /api/admin/prescriptions/pending-count
     * Returns count of prescriptions awaiting review (for dashboard KPI).
     */
    public Map<String, Long> getPendingCount() {
        return catalogClient.getPendingPrescriptionCount();
    }
}