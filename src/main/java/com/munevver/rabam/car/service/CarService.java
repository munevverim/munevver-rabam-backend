package com.munevver.rabam.car.service;

import com.munevver.rabam.car.dto.CarRequest;
import com.munevver.rabam.car.dto.CarResponse;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

public interface CarService {

    Page<CarResponse> getAllCars(Pageable pageable);

    CarResponse createCar(CarRequest request);

    CarResponse updateCar(Long id, CarRequest request);
}