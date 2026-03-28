package com.onlinePharmacy.admin.controller;

import com.onlinePharmacy.admin.dto.CategoryDto;
import com.onlinePharmacy.admin.dto.MedicineDto;
import com.onlinePharmacy.admin.service.AdminCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PRD Section 12: Admin Medicine Management
 * Exposes:
 *   GET    /api/admin/medicines          — search/list with pagination
 *   GET    /api/admin/medicines/{id}     — get medicine details
 *   POST   /api/admin/medicines          — create medicine
 *   PUT    /api/admin/medicines/{id}     — update medicine
 *   DELETE /api/admin/medicines/{id}     — soft-delete medicine
 *   GET    /api/admin/medicines/low-stock
 *   GET    /api/admin/medicines/expiring
 *   GET    /api/admin/categories         — list categories
 *   POST   /api/admin/categories         — create category
 *   PUT    /api/admin/categories/{id}    — update category
 *   DELETE /api/admin/categories/{id}    — soft-delete category
 *
 * All requests are forwarded to catalog-service via OpenFeign.
 * Every write action is recorded in the admin_audit_logs table.
 * The @PreAuthorize here is defence-in-depth — the gateway already blocks non-ADMIN.
 */
@RestController
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Medicine Management", description = "PRD §12 — Medicine and category CRUD")
public class AdminMedicineController {

    private final AdminCatalogService adminCatalogService;

    public AdminMedicineController(AdminCatalogService adminCatalogService) {
        this.adminCatalogService = adminCatalogService;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  MEDICINES   /api/admin/medicines/*
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/api/admin/medicines")
    @Operation(summary = "Search and list all medicines (paginated)")
    public ResponseEntity<Map<String, Object>> searchMedicines(
            @RequestParam(required = false)    String name,
            @RequestParam(required = false)    Long   categoryId,
            @RequestParam(defaultValue = "0")  int    page,
            @RequestParam(defaultValue = "10") int    size) {
        return ResponseEntity.ok(
                adminCatalogService.searchMedicines(name, categoryId, page, size));
    }

    @GetMapping("/api/admin/medicines/{id}")
    @Operation(summary = "Get medicine by ID")
    public ResponseEntity<MedicineDto> getMedicineById(
            @PathVariable Long id) {
        return ResponseEntity.ok(adminCatalogService.getMedicineById(id));
    }

    @PostMapping("/api/admin/medicines")
    @Operation(summary = "Create a new medicine")
    public ResponseEntity<MedicineDto> createMedicine(
            @RequestBody Object request,
            @RequestHeader("X-User-Id")     Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminCatalogService.createMedicine(request, adminId, adminEmail));
    }

    @PutMapping("/api/admin/medicines/{id}")
    @Operation(summary = "Update an existing medicine")
    public ResponseEntity<MedicineDto> updateMedicine(
            @PathVariable                   Long   id,
            @RequestBody                    Object request,
            @RequestHeader("X-User-Id")     Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        return ResponseEntity.ok(
                adminCatalogService.updateMedicine(id, request, adminId, adminEmail));
    }

    @DeleteMapping("/api/admin/medicines/{id}")
    @Operation(summary = "Soft-delete a medicine")
    public ResponseEntity<Void> deleteMedicine(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        adminCatalogService.deleteMedicine(id, adminId, adminEmail);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/medicines/low-stock")
    @Operation(summary = "Get medicines below stock threshold (default 10)")
    public ResponseEntity<List<MedicineDto>> getLowStock(
            @RequestParam(defaultValue = "10") int    threshold) {
        return ResponseEntity.ok(adminCatalogService.getLowStockMedicines(threshold));
    }

    @GetMapping("/api/admin/medicines/expiring")
    @Operation(summary = "Get medicines expiring within N days (default 30)")
    public ResponseEntity<List<MedicineDto>> getExpiring(
            @RequestParam(defaultValue = "30") int    daysAhead) {
        return ResponseEntity.ok(adminCatalogService.getExpiringMedicines(daysAhead));
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  CATEGORIES   /api/admin/categories/*
    // ══════════════════════════════════════════════════════════════════════════

    @GetMapping("/api/admin/categories")
    @Operation(summary = "Get all active categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories() {
        return ResponseEntity.ok(adminCatalogService.getAllCategories());
    }

    @PostMapping("/api/admin/categories")
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryDto> createCategory(
            @RequestBody                    Object request,
            @RequestHeader("X-User-Id")     Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminCatalogService.createCategory(request, adminId, adminEmail));
    }

    @PutMapping("/api/admin/categories/{id}")
    @Operation(summary = "Update a category")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable                   Long   id,
            @RequestBody                    Object request,
            @RequestHeader("X-User-Id")     Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        return ResponseEntity.ok(
                adminCatalogService.updateCategory(id, request, adminId, adminEmail));
    }

    @DeleteMapping("/api/admin/categories/{id}")
    @Operation(summary = "Soft-delete a category")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable                   Long   id,
            @RequestHeader("X-User-Id")     Long   adminId,
            @RequestHeader("X-User-Email")  String adminEmail) {
        adminCatalogService.deleteCategory(id, adminId, adminEmail);
        return ResponseEntity.noContent().build();
    }
}