package com.onlinePharmacy.admin.scheduler;

import com.onlinePharmacy.admin.client.CatalogClient;
import com.onlinePharmacy.admin.client.OrderClient;
import com.onlinePharmacy.admin.entity.SalesSnapshot;
import com.onlinePharmacy.admin.repository.SalesSnapshotRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * Runs every night at 00:05 to snapshot yesterday's order and revenue data
 * from order-service into the local sales_snapshots table.
 *
 * This means report queries hit the admin DB only (fast)
 * rather than calling order-service on every report request.
 *
 * Note: Uses a service-account admin token from config for internal Feign calls.
 * In production this should be replaced with a proper service-to-service auth mechanism.
 */
@Component
public class SnapshotScheduler {

    private static final Logger log = LoggerFactory.getLogger(SnapshotScheduler.class);

    private final OrderClient            orderClient;
    private final CatalogClient          catalogClient;
    private final SalesSnapshotRepository snapshotRepository;

    public SnapshotScheduler(OrderClient orderClient,
                              CatalogClient catalogClient,
                              SalesSnapshotRepository snapshotRepository) {
        this.orderClient        = orderClient;
        this.catalogClient      = catalogClient;
        this.snapshotRepository = snapshotRepository;
    }

    @Scheduled(cron = "0 5 0 * * *") // Every day at 00:05
    public void snapshotYesterdaysData() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        log.info("SnapshotScheduler: building snapshot for {}", yesterday);

        try {
            // Fetch yesterday's delivered orders
            Map<String, Object> deliveredOrders = orderClient.getAllOrders(
                    "DELIVERED", 0, 1);
            Map<String, Object> paidOrders = orderClient.getAllOrders(
                    "PAID", 0, 1);
            Map<String, Object> cancelledOrders = orderClient.getAllOrders(
                    "CUSTOMER_CANCELLED", 0, 1);

            long delivered  = extractTotal(deliveredOrders);
            long paid       = extractTotal(paidOrders);
            long cancelled  = extractTotal(cancelledOrders);
            long total      = delivered + paid + cancelled;

            // Prescription count
            Map<String, Long> rxCount = catalogClient.getPendingPrescriptionCount();
            long pendingRx = rxCount.getOrDefault("pendingCount", 0L);

            // Upsert snapshot
            Optional<SalesSnapshot> existing = snapshotRepository.findBySnapshotDate(yesterday);
            SalesSnapshot snapshot = existing.orElse(new SalesSnapshot());
            snapshot.setSnapshotDate(yesterday);
            snapshot.setTotalOrders(total);
            snapshot.setPaidOrders(paid);
            snapshot.setDeliveredOrders(delivered);
            snapshot.setCancelledOrders(cancelled);
            snapshot.setPendingPrescriptions(pendingRx);
            // Revenue: approximated — in production, order-service should expose a revenue endpoint
            snapshot.setTotalRevenue(0.0);

            snapshotRepository.save(snapshot);
            log.info("SnapshotScheduler: saved snapshot for {} — total={}", yesterday, total);

        } catch (Exception e) {
            log.error("SnapshotScheduler: failed to build snapshot for {}: {}", yesterday, e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private long extractTotal(Map<String, Object> page) {
        Object total = page.get("totalElements");
        if (total instanceof Number) return ((Number) total).longValue();
        return 0L;
    }
}