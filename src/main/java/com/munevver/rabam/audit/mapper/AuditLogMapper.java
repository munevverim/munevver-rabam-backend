package com.munevver.rabam.audit.mapper;

import com.munevver.rabam.audit.dto.AuditLogResponse;
import com.munevver.rabam.audit.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    public AuditLogResponse toResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .eventType(auditLog.getEventType())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .payload(auditLog.getPayload())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}