package com.munevver.rabam.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DashboardSummaryResponse {

    private Long totalCars;

    private Long totalServices;

    private Long pendingServices;

    private Long inProgressServices;

    private Long doneServices;
}
