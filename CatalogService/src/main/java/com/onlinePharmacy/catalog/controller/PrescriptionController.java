package com.onlinePharmacy.catalog.controller;



import com.onlinePharmacy.catalog.dto.PrescriptionResponse;
import com.onlinePharmacy.catalog.entity.PrescriptionStatus;
import com.onlinePharmacy.catalog.services.PrescriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/catalog/prescriptions")
@Tag(name = "Prescriptions", description = "Prescription upload and management endpoints")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // ── Customer Endpoints ────────────────────────────────────────────────────

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a prescription (PDF/JPG/PNG, max 5MB)")
    public ResponseEntity<PrescriptionResponse> upload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(required = false) Long medicineId) {
        PrescriptionResponse response = prescriptionService.uploadPrescription(userId, medicineId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user's prescriptions")
    public ResponseEntity<List<PrescriptionResponse>> getMyPrescriptions(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(prescriptionService.getUserPrescriptions(userId));
    }

    @GetMapping("/my/{id}")
    @Operation(summary = "Get a specific prescription by ID for the current user")
    public ResponseEntity<PrescriptionResponse> getMyPrescriptionById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id, userId));
    }

    // ── Admin Endpoints ───────────────────────────────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all prescriptions by status (Admin only)")
    public ResponseEntity<Page<PrescriptionResponse>> getAllByStatus(
            @RequestParam(defaultValue = "PENDING") PrescriptionStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByStatus(status, pageable));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approve or reject a prescription (Admin only)")
    public ResponseEntity<PrescriptionResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam PrescriptionStatus status,
            @RequestParam(required = false) String rejectionReason,
            @RequestHeader("X-User-Id") Long adminId) {
        return ResponseEntity.ok(
                prescriptionService.updatePrescriptionStatus(id, status, rejectionReason, adminId));
    }

    @GetMapping("/pending-count")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Count pending prescriptions (Admin dashboard)")
    public ResponseEntity<Map<String, Long>> pendingCount() {
        return ResponseEntity.ok(Map.of("pendingCount", prescriptionService.countPendingPrescriptions()));
    }
}