package com.munevver.rabam.audit.service;

import com.munevver.rabam.audit.dto.AuditLogResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {

    Page<AuditLogResponse> getAuditLogs(Pageable pageable);
}