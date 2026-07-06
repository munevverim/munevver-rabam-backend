package com.munevver.rabam.service.service;

import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.common.exception.BadRequestException;
import com.munevver.rabam.common.exception.ConflictException;
import com.munevver.rabam.common.exception.ResourceNotFoundException;
import com.munevver.rabam.common.i18n.I18nMessageService;
import com.munevver.rabam.event.dto.DomainEvent;
import com.munevver.rabam.event.enums.DomainEventType;
import com.munevver.rabam.event.enums.EntityType;
import com.munevver.rabam.event.publisher.DomainEventPublisher;
import com.munevver.rabam.service.dto.ServiceRequest;
import com.munevver.rabam.service.dto.ServiceResponse;
import com.munevver.rabam.service.dto.ServiceUpdateRequest;
import com.munevver.rabam.service.entity.Service;
import com.munevver.rabam.service.enums.ServiceStatus;
import com.munevver.rabam.service.repository.ServiceRepository;
import com.munevver.rabam.service.specification.ServiceSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private static final int MAX_ACTIVE_SERVICES_PER_CAR = 2;

    private final ServiceRepository serviceRepository;
    private final CarRepository carRepository;
    private final ServiceStatusTransitionValidator statusTransitionValidator;
    private final DomainEventPublisher domainEventPublisher;
    private final I18nMessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public Page<ServiceResponse> getAllServices(Long carId, ServiceStatus status, Pageable pageable) {
        return serviceRepository.findAll(
                ServiceSpecification.hasCarId(carId)
                        .and(ServiceSpecification.hasStatus(status)),
                pageable
        ).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("service.not.found", id)
                ));

        return mapToResponse(service);
    }

    @Override
    @Transactional
    public ServiceResponse createService(ServiceRequest request) {
        Car car = carRepository.findById(request.getCarId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("service.car.not.found", request.getCarId())
                ));

        Service service = new Service();
        service.setTitle(request.getTitle());
        service.setDescription(request.getDescription());
        service.setStatus(ServiceStatus.PENDING);
        service.setCar(car);

        Service savedService = serviceRepository.save(service);

        domainEventPublisher.publish(buildServiceEvent(DomainEventType.SERVICE_CREATED, savedService));

        return mapToResponse(savedService);
    }

    @Override
    @Transactional
    public ServiceResponse updateService(Long id, ServiceUpdateRequest request) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("service.not.found", id)
                ));

        if (request.getVersion() != null && !request.getVersion().equals(service.getVersion())) {
            throw new ConflictException(
                    messageService.getMessage("service.version.conflict")
            );
        }

        if (request.getStatus() != null) {
            statusTransitionValidator.validate(service.getStatus(), request.getStatus());

            if (request.getStatus() == ServiceStatus.IN_PROGRESS) {
                validateMaxActiveServices(service.getCar().getId());
            }

            service.setStatus(request.getStatus());
        }

        if (request.getTitle() != null) {
            service.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            service.setDescription(request.getDescription());
        }

        Service updatedService = serviceRepository.save(service);

        domainEventPublisher.publish(buildServiceEvent(DomainEventType.SERVICE_UPDATED, updatedService));

        return mapToResponse(updatedService);
    }

    private void validateMaxActiveServices(Long carId) {
        carRepository.findByIdForUpdate(carId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("service.car.not.found", carId)
                ));

        int activeCount = serviceRepository
                .findByCarIdAndStatusForUpdate(carId, ServiceStatus.IN_PROGRESS)
                .size();

        if (activeCount >= MAX_ACTIVE_SERVICES_PER_CAR) {
            throw new BadRequestException(
                    messageService.getMessage("service.max.active", MAX_ACTIVE_SERVICES_PER_CAR)
            );
        }
    }

    private ServiceResponse mapToResponse(Service service) {
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

    private DomainEvent buildServiceEvent(DomainEventType eventType, Service service) {
        Car car = service.getCar();

        return DomainEvent.builder()
                .eventType(eventType)
                .entityType(EntityType.SERVICE)
                .entityId(service.getId())
                .timestamp(LocalDateTime.now())
                .payload(Map.of(
                        "id", service.getId(),
                        "title", service.getTitle(),
                        "description", service.getDescription() == null ? "" : service.getDescription(),
                        "status", service.getStatus().name(),
                        "version", service.getVersion(),
                        "carId", car.getId(),
                        "carLicensePlate", car.getLicensePlate()
                ))
                .build();
    }
}