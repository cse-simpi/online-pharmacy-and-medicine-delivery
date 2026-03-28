package com.onlinePharmacy.admin.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.onlinePharmacy.admin.dto.AuditLogResponse;
import com.onlinePharmacy.admin.entity.AdminAuditLog;
import com.onlinePharmacy.admin.entity.AdminAuditLog.AdminAction;
import com.onlinePharmacy.admin.repository.AdminAuditLogRepository;

@Service
public class AuditLogService {

    private final AdminAuditLogRepository auditLogRepository;

    public AuditLogService(AdminAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long adminId, String adminEmail, AdminAction action,
                    String entityType, Long entityId, String description) {
        auditLogRepository.save(
                new AdminAuditLog(adminId, adminEmail, action, entityType, entityId, description));
    }

    public Page<AuditLogResponse> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAllByOrderByPerformedAtDesc(pageable)
                .map(this::toResponse);
    }

    public Page<AuditLogResponse> getLogsByAdmin(Long adminId, Pageable pageable) {
        return auditLogRepository.findByAdminIdOrderByPerformedAtDesc(adminId, pageable)
                .map(this::toResponse);
    }

    public Page<AuditLogResponse> getLogsByEntity(String entityType, Long entityId,
                                                   Pageable pageable) {
        return auditLogRepository
                .findByEntityTypeAndEntityIdOrderByPerformedAtDesc(entityType, entityId, pageable)
                .map(this::toResponse);
    }

    private AuditLogResponse toResponse(AdminAuditLog log) {
        AuditLogResponse dto = new AuditLogResponse();
        dto.setId(log.getId());
        dto.setAdminId(log.getAdminId());
        dto.setAdminEmail(log.getAdminEmail());
        dto.setAction(log.getAction().name());
        dto.setEntityType(log.getEntityType());
        dto.setEntityId(log.getEntityId());
        dto.setDescription(log.getDescription());
        dto.setPerformedAt(log.getPerformedAt());
        return dto;
    }
}