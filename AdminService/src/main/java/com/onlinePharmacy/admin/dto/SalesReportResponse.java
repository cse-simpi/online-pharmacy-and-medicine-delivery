package com.onlinePharmacy.admin.dto;

import java.time.LocalDate;
import java.util.List;

public class SalesReportResponse {

    private LocalDate from;
    private LocalDate to;
    private long      totalOrders;
    private double    totalRevenue;
    private long      totalDelivered;
    private long      totalCancelled;
    private long      prescriptionsProcessed;
    private List<DailyBreakdown> dailyBreakdown;

    public static class DailyBreakdown {
        private LocalDate date;
        private long      orders;
        private double    revenue;
        private long      delivered;
        private long      cancelled;

        public DailyBreakdown() {}
        public DailyBreakdown(LocalDate date, long orders, double revenue,
                              long delivered, long cancelled) {
            this.date = date; this.orders = orders; this.revenue = revenue;
            this.delivered = delivered; this.cancelled = cancelled;
        }

        public LocalDate getDate() { return date; }
        public long getOrders() { return orders; }
        public double getRevenue() { return revenue; }
        public long getDelivered() { return delivered; }
        public long getCancelled() { return cancelled; }
    }

    public SalesReportResponse() {}

    public LocalDate getFrom() { return from; }
    public void setFrom(LocalDate from) { this.from = from; }
    public LocalDate getTo() { return to; }
    public void setTo(LocalDate to) { this.to = to; }
    public long getTotalOrders() { return totalOrders; }
    public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }
    public long getTotalDelivered() { return totalDelivered; }
    public void setTotalDelivered(long totalDelivered) { this.totalDelivered = totalDelivered; }
    public long getTotalCancelled() { return totalCancelled; }
    public void setTotalCancelled(long totalCancelled) { this.totalCancelled = totalCancelled; }
    public long getPrescriptionsProcessed() { return prescriptionsProcessed; }
    public void setPrescriptionsProcessed(long v) { this.prescriptionsProcessed = v; }
    public List<DailyBreakdown> getDailyBreakdown() { return dailyBreakdown; }
    public void setDailyBreakdown(List<DailyBreakdown> dailyBreakdown) { this.dailyBreakdown = dailyBreakdown; }
}