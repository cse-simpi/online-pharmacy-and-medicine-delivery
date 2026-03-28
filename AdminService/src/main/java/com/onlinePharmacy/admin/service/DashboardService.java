package com.onlinePharmacy.admin.service;


import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.onlinePharmacy.admin.client.CatalogClient;
import com.onlinePharmacy.admin.dto.DashboardResponse;
import com.onlinePharmacy.admin.entity.AdminAuditLog.AdminAction;
import com.onlinePharmacy.admin.repository.AdminAuditLogRepository;
import com.onlinePharmacy.admin.repository.SalesSnapshotRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class DashboardService {

    private final CatalogClient catalogClient;
    private final AdminAuditLogRepository auditLogRepository;
    private final SalesSnapshotRepository salesSnapshotRepository;

    public DashboardService(CatalogClient catalogClient,
                            AdminAuditLogRepository auditLogRepository,
                            SalesSnapshotRepository salesSnapshotRepository) {
        this.catalogClient          = catalogClient;
      
        this.auditLogRepository     = auditLogRepository;
        this.salesSnapshotRepository = salesSnapshotRepository;
    }

    // Cached for 60 seconds to avoid hammering downstream services on every refresh
    @Cacheable(value = "dashboard", key = "'global'")
    public DashboardResponse getDashboard() {
        DashboardResponse dashboard = new DashboardResponse();
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDate today            = LocalDate.now();
        LocalDate firstOfMonth     = today.withDayOfMonth(1);

        // ── Revenue + Orders from snapshot table (populated by scheduler) ─────
        Double revenueToday = salesSnapshotRepository.sumRevenueBetween(today, today);
        dashboard.setRevenueToday(revenueToday != null ? revenueToday : 0.0);

        Double revenueMonth = salesSnapshotRepository.sumRevenueBetween(firstOfMonth, today);
        dashboard.setRevenueThisMonth(revenueMonth != null ? revenueMonth : 0.0);

        Long ordersToday = salesSnapshotRepository.sumOrdersBetween(today, today);
        dashboard.setTotalOrdersToday(ordersToday != null ? ordersToday : 0L);

        Long ordersMonth = salesSnapshotRepository.sumOrdersBetween(firstOfMonth, today);
        dashboard.setTotalOrdersThisMonth(ordersMonth != null ? ordersMonth : 0L);

        // ── Admin audit counters (from own DB — always fast) ──────────────────
        dashboard.setAdminActionsToday(
                auditLogRepository.countActionsSince(startOfToday));
        dashboard.setPrescriptionsApprovedToday(
                auditLogRepository.countByActionSince(
                        AdminAction.PRESCRIPTION_APPROVED, startOfToday));
        dashboard.setPrescriptionsRejectedToday(
                auditLogRepository.countByActionSince(
                        AdminAction.PRESCRIPTION_REJECTED, startOfToday));

        // ── Live counts from catalog-service via Feign ─────────────────────────
        try {
            Map<String, Long> pendingRx = catalogClient.getPendingPrescriptionCount();
            dashboard.setPendingPrescriptions(pendingRx.getOrDefault("pendingCount", 0L));

            dashboard.setLowStockMedicines(
                    catalogClient.getLowStockMedicines(10).size());
            dashboard.setExpiringMedicines(
                    catalogClient.getExpiringMedicines(30).size());
        } catch (Exception e) {
            // catalog-service down — degrade gracefully
            dashboard.setPendingPrescriptions(-1L);
            dashboard.setLowStockMedicines(-1L);
            dashboard.setExpiringMedicines(-1L);
        }

        return dashboard;
    }
}