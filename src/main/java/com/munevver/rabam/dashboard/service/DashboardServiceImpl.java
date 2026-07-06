package com.munevver.rabam.dashboard.service;

import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.dashboard.dto.DashboardSummaryResponse;
import com.munevver.rabam.service.enums.ServiceStatus;
import com.munevver.rabam.service.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final CarRepository carRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getSummary() {
        return DashboardSummaryResponse.builder()
                .totalCars(carRepository.count())
                .totalServices(serviceRepository.count())
                .pendingServices(serviceRepository.countByStatus(ServiceStatus.PENDING))
                .inProgressServices(serviceRepository.countByStatus(ServiceStatus.IN_PROGRESS))
                .doneServices(serviceRepository.countByStatus(ServiceStatus.DONE))
                .build();
    }
}