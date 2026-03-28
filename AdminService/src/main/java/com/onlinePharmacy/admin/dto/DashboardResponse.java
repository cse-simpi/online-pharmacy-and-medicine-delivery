package com.onlinePharmacy.admin.dto;

import java.util.List;

public class DashboardResponse {

    private long   totalOrdersToday;
    private long   totalOrdersThisMonth;
    private double revenueToday;
    private double revenueThisMonth;
    private long   pendingPrescriptions;
    private long   lowStockMedicines;
    private long   expiringMedicines;
    private long   adminActionsToday;
    private long   prescriptionsApprovedToday;
    private long   prescriptionsRejectedToday;

    public DashboardResponse() {}

    public long getTotalOrdersToday() { return totalOrdersToday; }
    public void setTotalOrdersToday(long v) { this.totalOrdersToday = v; }
    public long getTotalOrdersThisMonth() { return totalOrdersThisMonth; }
    public void setTotalOrdersThisMonth(long v) { this.totalOrdersThisMonth = v; }
    public double getRevenueToday() { return revenueToday; }
    public void setRevenueToday(double v) { this.revenueToday = v; }
    public double getRevenueThisMonth() { return revenueThisMonth; }
    public void setRevenueThisMonth(double v) { this.revenueThisMonth = v; }
    public long getPendingPrescriptions() { return pendingPrescriptions; }
    public void setPendingPrescriptions(long v) { this.pendingPrescriptions = v; }
    public long getLowStockMedicines() { return lowStockMedicines; }
    public void setLowStockMedicines(long v) { this.lowStockMedicines = v; }
    public long getExpiringMedicines() { return expiringMedicines; }
    public void setExpiringMedicines(long v) { this.expiringMedicines = v; }
    public long getAdminActionsToday() { return adminActionsToday; }
    public void setAdminActionsToday(long v) { this.adminActionsToday = v; }
    public long getPrescriptionsApprovedToday() { return prescriptionsApprovedToday; }
    public void setPrescriptionsApprovedToday(long v) { this.prescriptionsApprovedToday = v; }
    public long getPrescriptionsRejectedToday() { return prescriptionsRejectedToday; }
    public void setPrescriptionsRejectedToday(long v) { this.prescriptionsRejectedToday = v; }
}