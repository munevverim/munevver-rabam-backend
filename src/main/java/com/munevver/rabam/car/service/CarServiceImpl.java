package com.munevver.rabam.car.service;

import com.munevver.rabam.car.dto.CarRequest;
import com.munevver.rabam.car.dto.CarResponse;
import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.common.exception.ConflictException;
import com.munevver.rabam.common.exception.ResourceNotFoundException;
import com.munevver.rabam.event.dto.DomainEvent;
import com.munevver.rabam.event.enums.DomainEventType;
import com.munevver.rabam.event.enums.EntityType;
import com.munevver.rabam.event.publisher.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Override
    public Page<CarResponse> getAllCars(Pageable pageable) {
        return carRepository.findAll(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional
    public CarResponse createCar(CarRequest request) {
        String normalizedPlate = normalizePlate(request.getLicensePlate());

        if (carRepository.existsByLicensePlateIgnoreCase(normalizedPlate)) {
            throw new ConflictException("License plate already exists: " + normalizedPlate);
        }

        Car car = new Car();
        car.setLicensePlate(normalizedPlate);
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());

        Car savedCar = carRepository.save(car);

        domainEventPublisher.publish(buildCarEvent(DomainEventType.CAR_CREATED, savedCar));

        return mapToResponse(savedCar);
    }

    @Override
    @Transactional
    public CarResponse updateCar(Long id, CarRequest request) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found with id: " + id));

        String normalizedPlate = normalizePlate(request.getLicensePlate());

        boolean plateChanged = !car.getLicensePlate().equalsIgnoreCase(normalizedPlate);

        if (plateChanged && carRepository.existsByLicensePlateIgnoreCase(normalizedPlate)) {
            throw new ConflictException("License plate already exists: " + normalizedPlate);
        }

        car.setLicensePlate(normalizedPlate);
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());

        Car updatedCar = carRepository.save(car);

        domainEventPublisher.publish(buildCarEvent(DomainEventType.CAR_UPDATED, updatedCar));

        return mapToResponse(updatedCar);
    }

    private CarResponse mapToResponse(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .licensePlate(car.getLicensePlate())
                .brand(car.getBrand())
                .model(car.getModel())
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .build();
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