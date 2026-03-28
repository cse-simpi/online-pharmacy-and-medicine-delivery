package com.onlinePharmacy.admin.controller;

import com.onlinePharmacy.admin.dto.DashboardResponse;
import com.onlinePharmacy.admin.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * PRD Section 11: Admin Dashboard
 * Exposes: GET /api/admin/dashboard
 *
 * KPIs served:
 *  - Orders today / this month         ← from local sales_snapshots table
 *  - Revenue today / this month        ← from local sales_snapshots table
 *  - Pending prescriptions             ← Feign → catalog-service
 *  - Low stock medicines count         ← Feign → catalog-service
 *  - Expiring medicines count          ← Feign → catalog-service
 *  - Admin actions today               ← from local admin_audit_logs table
 *  - Prescriptions approved/rejected   ← from local admin_audit_logs table
 */
@RestController
@RequestMapping("/api/admin/dashboard")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Dashboard", description = "PRD §11 — KPI dashboard")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Get admin dashboard KPIs")
    public ResponseEntity<DashboardResponse> getDashboard(
            @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(dashboardService.getDashboard());
    }
}