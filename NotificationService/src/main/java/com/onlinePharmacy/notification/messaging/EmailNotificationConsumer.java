package com.onlinePharmacy.notification.messaging;

import com.onlinePharmacy.notification.config.RabbitMQConstants;
import com.onlinePharmacy.notification.messaging.event.OrderPlacedEvent;
import com.onlinePharmacy.notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationConsumer.class);

    private final EmailService emailService;

    public EmailNotificationConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Listens to the notification.order.placed.queue.
     * This queue is bound to the pharmacy.events exchange with routing key order.placed —
     * the same event order-service publishes when a checkout succeeds.
     *
     * Retry is configured in application.yml:
     *   spring.rabbitmq.listener.simple.retry.enabled=true (3 attempts, 2s backoff)
     * On final failure the message is routed to pharmacy.dlx dead-letter queue.
     */
    @RabbitListener(queues = RabbitMQConstants.Q_NOTIFICATION_ORDER_PLACED)
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Notification consumer received ORDER_PLACED: orderId={} orderNumber={} email={}",
                event.getOrderId(), event.getOrderNumber(), event.getUserEmail());
        emailService.sendOrderConfirmation(event);
    }
}