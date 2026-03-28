package com.onlinePharmacy.admin.repository;

import com.onlinePharmacy.admin.entity.AdminAuditLog;
import com.onlinePharmacy.admin.entity.AdminAuditLog.AdminAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface AdminAuditLogRepository extends JpaRepository<AdminAuditLog, Long> {
    Page<AdminAuditLog> findByAdminIdOrderByPerformedAtDesc(Long adminId, Pageable pageable);
    Page<AdminAuditLog> findByEntityTypeAndEntityIdOrderByPerformedAtDesc(
            String entityType, Long entityId, Pageable pageable);
    Page<AdminAuditLog> findAllByOrderByPerformedAtDesc(Pageable pageable);

    @Query("SELECT COUNT(a) FROM AdminAuditLog a WHERE a.performedAt >= :since")
    long countActionsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(a) FROM AdminAuditLog a WHERE a.action = :action AND a.performedAt >= :since")
    long countByActionSince(@Param("action") AdminAction action,
                            @Param("since") LocalDateTime since);
}