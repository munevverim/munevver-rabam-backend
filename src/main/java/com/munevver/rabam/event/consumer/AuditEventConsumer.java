package com.munevver.rabam.event.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.munevver.rabam.audit.entity.AuditLog;
import com.munevver.rabam.audit.repository.AuditLogRepository;
import com.munevver.rabam.event.dto.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditEventConsumer {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${app.rabbitmq.audit-queue}")
    public void consume(DomainEvent event) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setEventType(event.getEventType());
            auditLog.setEntityType(event.getEntityType());
            auditLog.setEntityId(event.getEntityId());
            auditLog.setCreatedAt(event.getTimestamp());
            auditLog.setPayload(objectMapper.writeValueAsString(event.getPayload()));

            auditLogRepository.save(auditLog);

            log.info(
                    "Audit log saved. eventType={}, entityType={}, entityId={}",
                    event.getEventType(),
                    event.getEntityType(),
                    event.getEntityId()
            );
        } catch (JsonProcessingException exception) {
            log.error("Audit event payload could not be serialized.", exception);
        } catch (Exception exception) {
            log.error(
                    "Audit event could not be saved. eventType={}, entityType={}, entityId={}",
                    event.getEventType(),
                    event.getEntityType(),
                    event.getEntityId(),
                    exception
            );
        }
    }
}