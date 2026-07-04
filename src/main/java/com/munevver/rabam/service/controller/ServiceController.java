package com.munevver.rabam.service.controller;

import com.munevver.rabam.common.response.ApiResponse;
import com.munevver.rabam.service.dto.ServiceRequest;
import com.munevver.rabam.service.dto.ServiceResponse;
import com.munevver.rabam.service.dto.ServiceUpdateRequest;
import com.munevver.rabam.service.enums.ServiceStatus;
import com.munevver.rabam.service.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public ApiResponse<Page<ServiceResponse>> getAllServices(
            @RequestParam(required = false) Long carId,
            @RequestParam(required = false) ServiceStatus status,
            Pageable pageable
    ) {
        return ApiResponse.success(
                "Services listed successfully",
                serviceService.getAllServices(carId, status, pageable)
        );
    }

    @PostMapping
    public ApiResponse<ServiceResponse> createService(@Valid @RequestBody ServiceRequest request) {
        return ApiResponse.success(
                "Service created successfully",
                serviceService.createService(request)
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<ServiceResponse> updateService(
            @PathVariable Long id,
            @Valid @RequestBody ServiceUpdateRequest request
    ) {
        return ApiResponse.success(
                "Service updated successfully",
                serviceService.updateService(id, request)
        );
    }

    @GetMapping("/{id}")
    public ApiResponse<ServiceResponse> getServiceById(@PathVariable Long id) {
        return ApiResponse.success(
                "Service retrieved successfully",
                serviceService.getServiceById(id)
        );
    }
}