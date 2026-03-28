package com.onlinePharmacy.admin.controller;

import com.onlinePharmacy.admin.dto.PrescriptionDto;
import com.onlinePharmacy.admin.service.AdminPrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * PRD Section 11+12: Admin Prescription Management
 * Exposes:
 *   GET   /api/admin/prescriptions               — paginated list filtered by status
 *   PATCH /api/admin/prescriptions/{id}/approve  — approve a prescription
 *   PATCH /api/admin/prescriptions/{id}/reject   — reject with reason
 *   GET   /api/admin/prescriptions/pending-count — count for dashboard KPI
 *
 * Delegates to catalog-service via OpenFeign.
 * Approve/Reject actions are recorded in admin_audit_logs.
 */
@RestController
@RequestMapping("/api/admin/prescriptions")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Prescriptions", description = "PRD §11+12 — Prescription queue management")
public class AdminPrescriptionController {

    private final AdminPrescriptionService adminPrescriptionService;

    public AdminPrescriptionController(AdminPrescriptionService adminPrescriptionService) {
        this.adminPrescriptionService = adminPrescriptionService;
    }

    @GetMapping
    @Operation(summary = "Get all prescriptions by status (default PENDING)")
    public ResponseEntity<Map<String, Object>> getPrescriptions(
            @RequestParam(defaultValue = "PENDING") String status,
            @RequestParam(defaultValue = "0")        int    page,
            @RequestParam(defaultValue = "10")       int    size) {
        return ResponseEntity.ok(
                adminPrescriptionService.getPrescriptions(status, page, size));
    }

    @PatchMapping("/{id}/approve")
    @Operation(summary = "Approve a prescription — logged in audit table")
    public ResponseEntity<PrescriptionDto> approvePrescription(
            @PathVariable                   Long   id,
            @RequestHeader("X-User-Id")     Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        return ResponseEntity.ok(
                adminPrescriptionService.approvePrescription(id,adminId, adminEmail));
    }

    @PatchMapping("/{id}/reject")
    @Operation(summary = "Reject a prescription with a reason — logged in audit table")
    public ResponseEntity<PrescriptionDto> rejectPrescription(
            @PathVariable                   Long   id,
            @RequestParam                   String reason,
            @RequestHeader("X-User-Id")     Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        return ResponseEntity.ok(
                adminPrescriptionService.rejectPrescription(id, reason,adminId, adminEmail));
    }

    @GetMapping("/pending-count")
    @Operation(summary = "Count of pending prescriptions (for dashboard KPI)")
    public ResponseEntity<Map<String, Long>> getPendingCount() {
        return ResponseEntity.ok(adminPrescriptionService.getPendingCount());
    }
}