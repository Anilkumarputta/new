package com.editorial.platform.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.editorial.platform.audit.model.AuditEntityType;
import com.editorial.platform.audit.model.AuditLog;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findAllByOrderByCreatedAtDesc();

    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(AuditEntityType entityType, Long entityId);
}
