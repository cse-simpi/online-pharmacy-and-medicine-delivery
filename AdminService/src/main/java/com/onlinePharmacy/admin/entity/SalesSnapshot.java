package com.onlinePharmacy.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales_snapshots",
       uniqueConstraints = @UniqueConstraint(columnNames = "snapshot_date"))
public class SalesSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "total_orders", nullable = false)
    private Long totalOrders = 0L;

    @Column(name = "total_revenue", nullable = false)
    private Double totalRevenue = 0.0;

    @Column(name = "paid_orders", nullable = false)
    private Long paidOrders = 0L;

    @Column(name = "cancelled_orders", nullable = false)
    private Long cancelledOrders = 0L;

    @Column(name = "delivered_orders", nullable = false)
    private Long deliveredOrders = 0L;

    @Column(name = "pending_prescriptions", nullable = false)
    private Long pendingPrescriptions = 0L;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public SalesSnapshot() {}

    public Long getId() { return id; }
    public LocalDate getSnapshotDate() { return snapshotDate; }
    public void setSnapshotDate(LocalDate snapshotDate) { this.snapshotDate = snapshotDate; }
    public Long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Long totalOrders) { this.totalOrders = totalOrders; }
    public Double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(Double totalRevenue) { this.totalRevenue = totalRevenue; }
    public Long getPaidOrders() { return paidOrders; }
    public void setPaidOrders(Long paidOrders) { this.paidOrders = paidOrders; }
    public Long getCancelledOrders() { return cancelledOrders; }
    public void setCancelledOrders(Long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
    public Long getDeliveredOrders() { return deliveredOrders; }
    public void setDeliveredOrders(Long deliveredOrders) { this.deliveredOrders = deliveredOrders; }
    public Long getPendingPrescriptions() { return pendingPrescriptions; }
    public void setPendingPrescriptions(Long pendingPrescriptions) { this.pendingPrescriptions = pendingPrescriptions; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}