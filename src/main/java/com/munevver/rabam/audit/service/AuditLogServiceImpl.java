package com.munevver.rabam.audit.service;

import com.munevver.rabam.audit.entity.AuditLog;
import com.munevver.rabam.audit.repository.AuditLogRepository;
import com.munevver.rabam.event.dto.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void save(DomainEvent event) {
        AuditLog auditLog = new AuditLog();

        auditLog.setEventType(event.getEventType().name());
        auditLog.setEntityType(event.getEntityType().name());
        auditLog.setEntityId(event.getEntityId());
        auditLog.setEventTimestamp(event.getTimestamp());
        auditLog.setPayload(convertPayloadToJson(event));

        auditLogRepository.save(auditLog);
    }

    private String convertPayloadToJson(DomainEvent event) {
        try {
            return objectMapper.writeValueAsString(event.getPayload());
        } catch (JacksonException e) {
            return "{}";
        }
    }
}