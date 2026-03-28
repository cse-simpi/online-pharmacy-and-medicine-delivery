package com.onlinePharmacy.notification.service;

import com.onlinePharmacy.notification.messaging.event.OrderPlacedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${notification.mail.from}")
    private String fromAddress;

    @Value("${notification.mail.from-name}")
    private String fromName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an HTML order confirmation email to the customer.
     * Called by EmailNotificationConsumer after consuming an OrderPlacedEvent.
     */
    public void sendOrderConfirmation(OrderPlacedEvent event) {
        if (event.getUserEmail() == null || event.getUserEmail().isBlank()) {
            log.warn("OrderPlacedEvent has no userEmail — skipping email for orderId={}",
                    event.getOrderId());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromAddress, fromName);
            helper.setTo(event.getUserEmail());
            helper.setSubject("Order Confirmed — " + event.getOrderNumber());
            helper.setText(buildHtmlBody(event), true); // true = isHtml

            mailSender.send(message);
            log.info("Order confirmation email sent to {} for order {}",
                    event.getUserEmail(), event.getOrderNumber());

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send order confirmation email for order {}: {}",
                    event.getOrderNumber(), e.getMessage());
            // Re-throw so RabbitMQ can retry (listener has retry configured)
            throw new RuntimeException("Email send failed: " + e.getMessage(), e);
        }
    }

    // ── HTML Email Template ───────────────────────────────────────────────────

    private String buildHtmlBody(OrderPlacedEvent event) {
        String name = event.getUserName() != null ? event.getUserName() : "Valued Customer";

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head>")
          .append("<meta charset='UTF-8'>")
          .append("<style>")
          .append("body{font-family:Arial,sans-serif;color:#333;margin:0;padding:0;background:#f4f4f4;}")
          .append(".container{max-width:600px;margin:30px auto;background:#fff;border-radius:8px;overflow:hidden;}")
          .append(".header{background:#1D9E75;padding:24px 32px;}")
          .append(".header h1{color:#fff;margin:0;font-size:22px;}")
          .append(".header p{color:#9FE1CB;margin:4px 0 0;font-size:14px;}")
          .append(".body{padding:28px 32px;}")
          .append(".body p{line-height:1.6;margin:0 0 14px;}")
          .append("table{width:100%;border-collapse:collapse;margin:16px 0;}")
          .append("th{background:#f0f0f0;padding:10px 12px;text-align:left;font-size:13px;border-bottom:2px solid #ddd;}")
          .append("td{padding:10px 12px;font-size:13px;border-bottom:1px solid #eee;}")
          .append(".total-row td{font-weight:bold;background:#f9f9f9;}")
          .append(".info-box{background:#f0faf6;border-left:4px solid #1D9E75;padding:14px 16px;border-radius:0 6px 6px 0;margin:16px 0;font-size:13px;}")
          .append(".warning{background:#fff8e1;border-left:4px solid #FFA000;padding:12px 16px;border-radius:0 6px 6px 0;margin:16px 0;font-size:13px;}")
          .append(".footer{background:#f4f4f4;padding:18px 32px;text-align:center;font-size:12px;color:#888;}")
          .append("</style></head><body>")

          // Header
          .append("<div class='container'>")
          .append("<div class='header'>")
          .append("<h1>Order Confirmed!</h1>")
          .append("<p>Order #").append(event.getOrderNumber()).append("</p>")
          .append("</div>")

          // Body
          .append("<div class='body'>")
          .append("<p>Hi <strong>").append(name).append("</strong>,</p>")
          .append("<p>Thank you for your order. We have received it and will process it shortly.</p>")

          // Order info box
          .append("<div class='info-box'>")
          .append("<strong>Order ID:</strong> ").append(event.getOrderNumber()).append("<br>")
          .append("<strong>Status:</strong> ").append(formatStatus(event.getStatus())).append("<br>");
        if (event.getDeliverySlot() != null) {
            sb.append("<strong>Delivery slot:</strong> ").append(event.getDeliverySlot()).append("<br>");
        }
        if (event.getDeliveryAddress() != null) {
            sb.append("<strong>Delivery address:</strong> ").append(event.getDeliveryAddress());
        }
        sb.append("</div>");

        // Prescription warning
        if (event.isHasPrescriptionItems()) {
            sb.append("<div class='warning'>")
              .append("Your order contains prescription medicine(s). Our pharmacist will verify your ")
              .append("prescription before dispatching. You will receive a confirmation once approved.")
              .append("</div>");
        }

        // Items table
        if (event.getItems() != null && !event.getItems().isEmpty()) {
            sb.append("<table>")
              .append("<tr><th>Medicine</th><th>Qty</th><th>Unit Price</th><th>Total</th></tr>");
            for (OrderPlacedEvent.OrderItemInfo item : event.getItems()) {
                sb.append("<tr>")
                  .append("<td>").append(item.getMedicineName()).append("</td>")
                  .append("<td>").append(item.getQuantity()).append("</td>")
                  .append("<td>&#8377;").append(String.format("%.2f", item.getUnitPrice())).append("</td>")
                  .append("<td>&#8377;").append(String.format("%.2f", item.getTotalPrice())).append("</td>")
                  .append("</tr>");
            }
            sb.append("<tr class='total-row'>")
              .append("<td colspan='3'>Order Total</td>")
              .append("<td>&#8377;").append(String.format("%.2f", event.getTotalAmount())).append("</td>")
              .append("</tr>")
              .append("</table>");
        } else {
            sb.append("<p><strong>Order Total: &#8377;")
              .append(String.format("%.2f", event.getTotalAmount()))
              .append("</strong></p>");
        }

        sb.append("<p>You can track your order status by logging into your account.</p>")
          .append("</div>")

          // Footer
          .append("<div class='footer'>")
          .append("Online Pharmacy &mdash; Delivering health to your door<br>")
          .append("This is an automated email, please do not reply.")
          .append("</div>")
          .append("</div>")
          .append("</body></html>");

        return sb.toString();
    }

    private String formatStatus(String status) {
        if (status == null) return "Processing";
        return switch (status) {
            case "PAYMENT_PENDING"      -> "Payment Pending";
            case "PRESCRIPTION_PENDING" -> "Awaiting Prescription Verification";
            default -> status.replace("_", " ");
        };
    }
}