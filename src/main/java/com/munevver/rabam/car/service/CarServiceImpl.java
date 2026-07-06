package com.munevver.rabam.car.service;

import com.munevver.rabam.car.dto.CarRequest;
import com.munevver.rabam.car.dto.CarResponse;
import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.car.mapper.CarMapper;
import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.common.exception.ConflictException;
import com.munevver.rabam.common.exception.ResourceNotFoundException;
import com.munevver.rabam.common.i18n.I18nMessageService;
import com.munevver.rabam.event.dto.DomainEvent;
import com.munevver.rabam.event.enums.DomainEventType;
import com.munevver.rabam.event.enums.EntityType;
import com.munevver.rabam.event.publisher.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final DomainEventPublisher domainEventPublisher;
    private final I18nMessageService messageService;

    @Override
    @Transactional(readOnly = true)
    public Page<CarResponse> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(carMapper::toResponse);
    }

    @Override
    @Transactional
    public CarResponse createCar(CarRequest request) {
        String normalizedPlate = normalizePlate(request.getLicensePlate());

        if (carRepository.existsByLicensePlateIgnoreCase(normalizedPlate)) {
            throw new ConflictException(
                    messageService.getMessage("car.license.exists", normalizedPlate)
            );
        }

        Car car = carMapper.toEntity(request, normalizedPlate);

        Car savedCar = carRepository.save(car);

        domainEventPublisher.publish(
                buildCarEvent(DomainEventType.CAR_CREATED, savedCar)
        );

        return carMapper.toResponse(savedCar);
    }

    @Override
    @Transactional
    public CarResponse updateCar(Long id, CarRequest request) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        messageService.getMessage("car.not.found", id)
                ));

        String normalizedPlate = normalizePlate(request.getLicensePlate());

        boolean plateChanged = !car.getLicensePlate().equalsIgnoreCase(normalizedPlate);

        if (plateChanged && carRepository.existsByLicensePlateIgnoreCase(normalizedPlate)) {
            throw new ConflictException(
                    messageService.getMessage("car.license.exists", normalizedPlate)
            );
        }

        carMapper.updateEntity(car, request, normalizedPlate);

        Car updatedCar = carRepository.save(car);

        domainEventPublisher.publish(
                buildCarEvent(DomainEventType.CAR_UPDATED, updatedCar)
        );

        return carMapper.toResponse(updatedCar);
    }

    private DomainEvent buildCarEvent(DomainEventType eventType, Car car) {
        return DomainEvent.builder()
                .eventType(eventType)
                .entityType(EntityType.CAR)
                .entityId(car.getId())
                .timestamp(LocalDateTime.now())
                .payload(Map.of(
                        "id", car.getId(),
                        "licensePlate", car.getLicensePlate(),
                        "brand", car.getBrand(),
                        "model", car.getModel()
                ))
                .build();
    }

    private String normalizePlate(String licensePlate) {
        return licensePlate.trim().toUpperCase();
    }
}