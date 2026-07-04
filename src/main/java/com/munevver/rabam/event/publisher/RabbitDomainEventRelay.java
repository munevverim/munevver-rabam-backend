package com.munevver.rabam.event.publisher;

import com.munevver.rabam.event.dto.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitDomainEventRelay {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.audit-routing-key}")
    private String auditRoutingKey;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishAfterCommit(DomainEvent event) {
        rabbitTemplate.convertAndSend(exchangeName, auditRoutingKey, event);

        log.info(
                "Domain event published to RabbitMQ. eventType={}, entityType={}, entityId={}",
                event.getEventType(),
                event.getEntityType(),
                event.getEntityId()
        );
    }
}