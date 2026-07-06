package com.munevver.rabam.audit.dto;

import com.munevver.rabam.event.enums.DomainEventType;
import com.munevver.rabam.event.enums.EntityType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AuditLogResponse {

    private Long id;
    private DomainEventType eventType;
    private EntityType entityType;
    private Long entityId;
    private String payload;
    private LocalDateTime createdAt;
}