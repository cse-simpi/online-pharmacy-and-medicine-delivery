package com.onlinePharmacy.notification.config;

/**
 * Mirrors the exchange name from order-service's RabbitMQConstants.
 * Only adds the new notification-specific queue.
 * The exchange already exists — declared by admin-service at startup.
 */
public final class RabbitMQConstants {

    private RabbitMQConstants() {}

    // Same exchange used by order-service and admin-service
    public static final String EXCHANGE = "pharmacy.events";

    // Same routing key order-service publishes with
    public static final String RK_ORDER_PLACED = "order.placed";

    // New queue — only notification-service listens here
    public static final String Q_NOTIFICATION_ORDER_PLACED = "notification.order.placed.queue";

    // Dead-letter exchange (shared infra — already exists)
    public static final String DLX = "pharmacy.dlx";
}