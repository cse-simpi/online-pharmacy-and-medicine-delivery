package com.onlinePharmacy.admin.service;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.onlinePharmacy.admin.client.CatalogClient;
import com.onlinePharmacy.admin.dto.MedicineDto;
import com.onlinePharmacy.admin.dto.SalesReportResponse;
import com.onlinePharmacy.admin.dto.SalesReportResponse.DailyBreakdown;
import com.onlinePharmacy.admin.entity.AdminAuditLog.AdminAction;
import com.onlinePharmacy.admin.entity.SalesSnapshot;
import com.onlinePharmacy.admin.repository.AdminAuditLogRepository;
import com.onlinePharmacy.admin.repository.SalesSnapshotRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminReportService {

    private final SalesSnapshotRepository  snapshotRepository;
    private final AdminAuditLogRepository  auditLogRepository;
    private final CatalogClient catalogClient;

    public AdminReportService(SalesSnapshotRepository snapshotRepository,
                              AdminAuditLogRepository auditLogRepository,
                              CatalogClient catalogClient) {
        this.snapshotRepository = snapshotRepository;
        this.auditLogRepository = auditLogRepository;
        this.catalogClient      = catalogClient;
    }

    /**
     * GET /api/admin/reports/sales
     * Reads from local sales_snapshots table — no Feign call needed.
     * Snapshots are built nightly by SnapshotScheduler.
     */
    @Cacheable(value = "reports", key = "#from + '-' + #to")
    public SalesReportResponse getSalesReport(LocalDate from, LocalDate to) {
        List<SalesSnapshot> snapshots =
                snapshotRepository.findBySnapshotDateBetweenOrderBySnapshotDateAsc(from, to);

        SalesReportResponse report = new SalesReportResponse();
        report.setFrom(from);
        report.setTo(to);

        long   totalOrders    = 0L;
        double totalRevenue   = 0.0;
        long   totalDelivered = 0L;
        long   totalCancelled = 0L;

        List<DailyBreakdown> daily = new ArrayList<>();

        // Fill in days that have snapshots
        for (SalesSnapshot s : snapshots) {
            totalOrders    += s.getTotalOrders();
            totalRevenue   += s.getTotalRevenue();
            totalDelivered += s.getDeliveredOrders();
            totalCancelled += s.getCancelledOrders();

            daily.add(new DailyBreakdown(
                    s.getSnapshotDate(),
                    s.getTotalOrders(),
                    s.getTotalRevenue(),
                    s.getDeliveredOrders(),
                    s.getCancelledOrders()
            ));
        }

        // Fill in days with no snapshot yet (today / future) as zero rows
        LocalDate cursor = from;
        List<LocalDate> coveredDates = snapshots.stream()
                .map(SalesSnapshot::getSnapshotDate).toList();
        while (!cursor.isAfter(to)) {
            if (!coveredDates.contains(cursor)) {
                daily.add(new DailyBreakdown(cursor, 0L, 0.0, 0L, 0L));
            }
            cursor = cursor.plusDays(1);
        }
        daily.sort((a, b) -> a.getDate().compareTo(b.getDate()));

        // Prescriptions processed = approved + rejected in range (from audit log)
        long prescriptionsProcessed =
                auditLogRepository.countByActionSince(
                        AdminAction.PRESCRIPTION_APPROVED, from.atStartOfDay()) +
                auditLogRepository.countByActionSince(
                        AdminAction.PRESCRIPTION_REJECTED, from.atStartOfDay());

        report.setTotalOrders(totalOrders);
        report.setTotalRevenue(totalRevenue);
        report.setTotalDelivered(totalDelivered);
        report.setTotalCancelled(totalCancelled);
        report.setPrescriptionsProcessed(prescriptionsProcessed);
        report.setDailyBreakdown(daily);

        return report;
    }

    /**
     * GET /api/admin/reports/inventory
     * Returns low-stock and expiring medicines from catalog-service.
     * Used for inventory health report.
     */
    public Object getInventoryReport(int lowStockThreshold, int expiringDays) {
        List<MedicineDto> lowStock  = catalogClient.getLowStockMedicines(lowStockThreshold);
        List<MedicineDto> expiring  = catalogClient.getExpiringMedicines(expiringDays);

        return java.util.Map.of(
                "lowStockCount",  lowStock.size(),
                "expiringCount",  expiring.size(),
                "lowStockItems",  lowStock,
                "expiringItems",  expiring
        );
    }

    /**
     * GET /api/admin/reports/prescriptions
     * Prescription volume stats from audit log table.
     */
    public Object getPrescriptionReport(LocalDate from, LocalDate to) {
        long approved = auditLogRepository.countByActionSince(
                AdminAction.PRESCRIPTION_APPROVED, from.atStartOfDay());
        long rejected = auditLogRepository.countByActionSince(
                AdminAction.PRESCRIPTION_REJECTED, from.atStartOfDay());

        return java.util.Map.of(
                "from",          from.toString(),
                "to",            to.toString(),
                "totalApproved", approved,
                "totalRejected", rejected,
                "totalProcessed",approved + rejected
        );
    }
}