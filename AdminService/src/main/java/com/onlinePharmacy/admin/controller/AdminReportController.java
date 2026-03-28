package com.onlinePharmacy.admin.controller;

import com.onlinePharmacy.admin.dto.AuditLogResponse;
import com.onlinePharmacy.admin.dto.SalesReportResponse;
import com.onlinePharmacy.admin.service.AdminReportService;
import com.onlinePharmacy.admin.service.AuditLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


/**
 * PRD Section 14: Admin Reports
 * Exposes:
 *   GET /api/admin/reports/sales          — daily sales with revenue + order counts
 *   GET /api/admin/reports/inventory      — low-stock + expiring medicines report
 *   GET /api/admin/reports/prescriptions  — prescription approval volume report
 *   GET /api/admin/reports/sales/export   — CSV export of sales data
 *   GET /api/admin/audit-logs             — admin action audit trail (also a report)
 */
@RestController
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin Reports", description = "PRD §14 — Sales, inventory, prescriptions, exports")
public class AdminReportController {

    private final AdminReportService adminReportService;
    private final AuditLogService    auditLogService;

    public AdminReportController(AdminReportService adminReportService,
                                 AuditLogService auditLogService) {
        this.adminReportService = adminReportService;
        this.auditLogService    = auditLogService;
    }

    // ── Sales Report ──────────────────────────────────────────────────────────

    @GetMapping("/api/admin/reports/sales")
    @Operation(summary = "Sales report with daily breakdown — from local snapshot table")
    public ResponseEntity<SalesReportResponse> getSalesReport(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(adminReportService.getSalesReport(from, to));
    }

    @GetMapping("/api/admin/reports/sales/export")
    @Operation(summary = "Export sales report as CSV")
    public ResponseEntity<String> exportSalesReportCsv(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        SalesReportResponse report = adminReportService.getSalesReport(from, to);

        StringBuilder csv = new StringBuilder();
        csv.append("Date,Orders,Revenue,Delivered,Cancelled\n");
        for (SalesReportResponse.DailyBreakdown day : report.getDailyBreakdown()) {
            csv.append(day.getDate()).append(",")
               .append(day.getOrders()).append(",")
               .append(String.format("%.2f", day.getRevenue())).append(",")
               .append(day.getDelivered()).append(",")
               .append(day.getCancelled()).append("\n");
        }
        csv.append("\nTOTAL,").append(report.getTotalOrders()).append(",")
           .append(String.format("%.2f", report.getTotalRevenue())).append(",")
           .append(report.getTotalDelivered()).append(",")
           .append(report.getTotalCancelled()).append("\n");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=sales-report-" + from + "-to-" + to + ".csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    // ── Inventory Report ──────────────────────────────────────────────────────

    @GetMapping("/api/admin/reports/inventory")
    @Operation(summary = "Inventory report — low stock + expiring medicines from catalog-service")
    public ResponseEntity<Object> getInventoryReport(
            @RequestParam(defaultValue = "10") int    lowStockThreshold,
            @RequestParam(defaultValue = "30") int    expiringDays) {
        return ResponseEntity.ok(
                adminReportService.getInventoryReport(lowStockThreshold, expiringDays));
    }

    // ── Prescription Volume Report ────────────────────────────────────────────

    @GetMapping("/api/admin/reports/prescriptions")
    @Operation(summary = "Prescription approval/rejection volume from admin audit log")
    public ResponseEntity<Object> getPrescriptionReport(
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().minusDays(30)}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(adminReportService.getPrescriptionReport(from, to));
    }

    // ── Audit Log ─────────────────────────────────────────────────────────────

    @GetMapping("/api/admin/audit-logs")
    @Operation(summary = "Admin action audit trail — who did what and when")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(auditLogService.getAllLogs(pageable));
    }

    @GetMapping("/api/admin/audit-logs/admin/{adminId}")
    @Operation(summary = "Audit logs for a specific admin user")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByAdmin(
            @PathVariable Long adminId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                auditLogService.getLogsByAdmin(adminId, PageRequest.of(page, size)));
    }

    @GetMapping("/api/admin/audit-logs/{entityType}/{entityId}")
    @Operation(summary = "Audit logs for a specific entity (ORDER, MEDICINE, PRESCRIPTION, CATEGORY)")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogsByEntity(
            @PathVariable String entityType,
            @PathVariable Long   entityId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(
                auditLogService.getLogsByEntity(entityType, entityId, PageRequest.of(page, size)));
    }
}