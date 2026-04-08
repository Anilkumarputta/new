package com.editorial.platform.audit.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.editorial.platform.audit.api.dto.AuditLogResponse;
import com.editorial.platform.audit.model.AuditEntityType;
import com.editorial.platform.audit.service.AuditLogService;
import com.editorial.platform.common.exception.BadRequestException;

@RestController
@RequestMapping("/api/v1/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLogResponse>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<List<AuditLogResponse>> getLogsForEntity(
        @PathVariable String entityType,
        @PathVariable Long entityId
    ) {
        AuditEntityType parsedType;
        try {
            parsedType = AuditEntityType.valueOf(entityType.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Entity type must be SHOW or WORKOUT");
        }
        return ResponseEntity.ok(auditLogService.getLogsForEntity(parsedType, entityId));
    }
}
