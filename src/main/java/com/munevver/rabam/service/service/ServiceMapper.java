package com.munevver.rabam.service.mapper;

import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.service.dto.ServiceRequest;
import com.munevver.rabam.service.dto.ServiceResponse;
import com.munevver.rabam.service.dto.ServiceUpdateRequest;
import com.munevver.rabam.service.entity.Service;
import com.munevver.rabam.service.enums.ServiceStatus;
import org.springframework.stereotype.Component;

@Component
public class ServiceMapper {

    public Service toEntity(ServiceRequest request, Car car) {
        Service service = new Service();
        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setStatus(ServiceStatus.PENDING);
        service.setCar(car);
        return service;
    }

    public void updateEntity(Service service, ServiceUpdateRequest request) {
        if (request.getTitle() != null) {
            service.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }

        if (request.getStatus() != null) {
            service.setStatus(request.getStatus());
        }
    }

    public ServiceResponse toResponse(Service service) {
        Car car = service.getCar();

        return ServiceResponse.builder()
                .id(service.getId())
                .title(service.getTitle())
                .description(service.getDescription())
                .status(service.getStatus())
                .carId(car.getId())
                .carLicensePlate(car.getLicensePlate())
                .carBrand(car.getBrand())
                .carModel(car.getModel())
                .version(service.getVersion())
                .createdAt(service.getCreatedAt())
                .updatedAt(service.getUpdatedAt())
                .build();
    }
}