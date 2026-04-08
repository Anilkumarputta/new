package com.editorial.platform.audit.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.editorial.platform.audit.api.dto.AuditLogResponse;
import com.editorial.platform.audit.model.AuditAction;
import com.editorial.platform.audit.model.AuditEntityType;
import com.editorial.platform.audit.model.AuditLog;
import com.editorial.platform.audit.repository.AuditLogRepository;
import com.editorial.platform.common.model.PublishingStatus;

@Service
@Transactional
public class AuditLogService {

    private static final String DEFAULT_ACTOR = "admin.user";

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logChange(
        AuditEntityType entityType,
        Long entityId,
        AuditAction action,
        PublishingStatus oldStatus,
        PublishingStatus newStatus,
        String message
    ) {
        AuditLog auditLog = new AuditLog();
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setAction(action);
        auditLog.setOldStatus(oldStatus != null ? oldStatus.name() : null);
        auditLog.setNewStatus(newStatus != null ? newStatus.name() : null);
        auditLog.setActorName(DEFAULT_ACTOR);
        auditLog.setMessage(message);

        auditLogRepository.save(auditLog);
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getAllLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AuditLogResponse> getLogsForEntity(AuditEntityType entityType, Long entityId) {
        return auditLogRepository.findByEntityTypeAndEntityIdOrderByCreatedAtDesc(entityType, entityId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    private AuditLogResponse toResponse(AuditLog auditLog) {
        AuditLogResponse response = new AuditLogResponse();
        response.setId(auditLog.getId());
        response.setEntityType(auditLog.getEntityType().name());
        response.setEntityId(auditLog.getEntityId());
        response.setAction(auditLog.getAction().name());
        response.setOldStatus(auditLog.getOldStatus());
        response.setNewStatus(auditLog.getNewStatus());
        response.setActorName(auditLog.getActorName());
        response.setMessage(auditLog.getMessage());
        response.setCreatedAt(auditLog.getCreatedAt());
        return response;
    }
}
