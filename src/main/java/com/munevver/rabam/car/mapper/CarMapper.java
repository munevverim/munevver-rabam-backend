package com.munevver.rabam.car.mapper;

import com.munevver.rabam.car.dto.CarRequest;
import com.munevver.rabam.car.dto.CarResponse;
import com.munevver.rabam.car.entity.Car;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

    public Car toEntity(CarRequest request, String normalizedPlate) {
        Car car = new Car();
        car.setLicensePlate(normalizedPlate);
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        return car;
    }

    public void updateEntity(Car car, CarRequest request, String normalizedPlate) {
        car.setLicensePlate(normalizedPlate);
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
    }

    public CarResponse toResponse(Car car) {
        return CarResponse.builder()
                .id(car.getId())
                .licensePlate(car.getLicensePlate())
                .brand(car.getBrand())
                .model(car.getModel())
                .createdAt(car.getCreatedAt())
                .updatedAt(car.getUpdatedAt())
                .build();
    }
}