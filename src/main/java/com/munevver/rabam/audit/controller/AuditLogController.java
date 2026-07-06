package com.munevver.rabam.audit.controller;

import com.munevver.rabam.audit.dto.AuditLogResponse;
import com.munevver.rabam.audit.service.AuditLogService;
import com.munevver.rabam.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLogResponse>>> getAuditLogs(
            @PageableDefault(
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<AuditLogResponse> auditLogs = auditLogService.getAuditLogs(pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Audit logs fetched successfully.", auditLogs)
        );
    }
}