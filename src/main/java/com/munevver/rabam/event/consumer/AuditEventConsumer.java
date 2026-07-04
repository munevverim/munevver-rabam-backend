package com.munevver.rabam.event.consumer;

import com.munevver.rabam.audit.service.AuditLogService;
import com.munevver.rabam.event.dto.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditEventConsumer {

    private final AuditLogService auditLogService;

    @RabbitListener(queues = "${app.rabbitmq.audit-queue}")
    public void consume(DomainEvent event) {
        auditLogService.save(event);

        log.info(
                "Audit event consumed and persisted. eventType={}, entityType={}, entityId={}",
                event.getEventType(),
                event.getEntityType(),
                event.getEntityId()
        );
    }
}