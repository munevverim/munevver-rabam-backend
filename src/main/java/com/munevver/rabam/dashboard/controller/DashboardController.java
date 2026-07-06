package com.munevver.rabam.dashboard.controller;

import com.munevver.rabam.common.response.ApiResponse;
import com.munevver.rabam.dashboard.dto.DashboardSummaryResponse;
import com.munevver.rabam.dashboard.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary() {
        DashboardSummaryResponse summary = dashboardService.getSummary();

        return ResponseEntity.ok(
                ApiResponse.success("Dashboard summary fetched successfully.", summary)
        );
    }
}