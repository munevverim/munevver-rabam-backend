package com.munevver.rabam.audit.service;

import com.munevver.rabam.event.dto.DomainEvent;

public interface AuditLogService {

    void save(DomainEvent event);
}