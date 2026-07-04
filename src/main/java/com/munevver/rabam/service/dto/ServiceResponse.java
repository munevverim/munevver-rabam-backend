package com.munevver.rabam.service.dto;

import com.munevver.rabam.service.enums.ServiceStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@Builder
public class ServiceResponse {

    private Long id;
    private String title;
    private String description;
    private ServiceStatus status;

    private Long carId;
    private String carLicensePlate;
    private String carBrand;
    private String carModel;

    private Long version;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
