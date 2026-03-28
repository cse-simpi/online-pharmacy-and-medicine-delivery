package com.onlinePharmacy.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "admin_audit_logs")
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "admin_id", nullable = false)
    private Long adminId;

    @Column(name = "admin_email", nullable = false)
    private String adminEmail;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AdminAction action;

    @Column(name = "entity_type", nullable = false)
    private String entityType;    // ORDER, PRESCRIPTION, MEDICINE, CATEGORY

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;   // human-readable: "Approved prescription #5 for userId=42"

    @Column(name = "performed_at", updatable = false)
    private LocalDateTime performedAt;

    @PrePersist
    protected void onCreate() {
        this.performedAt = LocalDateTime.now();
    }

    public enum AdminAction {
        ORDER_STATUS_UPDATED,
        PRESCRIPTION_APPROVED,
        PRESCRIPTION_REJECTED,
        MEDICINE_CREATED,
        MEDICINE_UPDATED,
        MEDICINE_DELETED,
        CATEGORY_CREATED,
        CATEGORY_UPDATED,
        CATEGORY_DELETED
    }

    public AdminAuditLog() {}

    public AdminAuditLog(Long adminId, String adminEmail, AdminAction action,
                         String entityType, Long entityId, String description) {
        this.adminId     = adminId;
        this.adminEmail  = adminEmail;
        this.action      = action;
        this.entityType  = entityType;
        this.entityId    = entityId;
        this.description = description;
    }

    public Long getId() { return id; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
    public String getAdminEmail() { return adminEmail; }
    public void setAdminEmail(String adminEmail) { this.adminEmail = adminEmail; }
    public AdminAction getAction() { return action; }
    public void setAction(AdminAction action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getPerformedAt() { return performedAt; }
}