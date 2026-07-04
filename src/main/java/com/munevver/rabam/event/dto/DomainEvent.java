package com.munevver.rabam.event.dto;

import com.munevver.rabam.event.enums.DomainEventType;
import com.munevver.rabam.event.enums.EntityType;

import java.time.LocalDateTime;
import java.util.Map;

public class DomainEvent {

    private DomainEventType eventType;
    private EntityType entityType;
    private Long entityId;
    private LocalDateTime timestamp;
    private Map<String, Object> payload;

    public DomainEvent() {
    }

    public DomainEvent(
            DomainEventType eventType,
            EntityType entityType,
            Long entityId,
            LocalDateTime timestamp,
            Map<String, Object> payload
    ) {
        this.eventType = eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    public static DomainEventBuilder builder() {
        return new DomainEventBuilder();
    }

    public DomainEventType getEventType() {
        return eventType;
    }

    public void setEventType(DomainEventType eventType) {
        this.eventType = eventType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }

    public static class DomainEventBuilder {
        private DomainEventType eventType;
        private EntityType entityType;
        private Long entityId;
        private LocalDateTime timestamp;
        private Map<String, Object> payload;

        public DomainEventBuilder eventType(DomainEventType eventType) {
            this.eventType = eventType;
            return this;
        }

        public DomainEventBuilder entityType(EntityType entityType) {
            this.entityType = entityType;
            return this;
        }

        public DomainEventBuilder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }

        public DomainEventBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public DomainEventBuilder payload(Map<String, Object> payload) {
            this.payload = payload;
            return this;
        }

        public DomainEvent build() {
            return new DomainEvent(eventType, entityType, entityId, timestamp, payload);
        }
    }
}