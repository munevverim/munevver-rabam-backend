package com.munevver.rabam.car.controller;

import com.munevver.rabam.car.dto.CarRequest;
import com.munevver.rabam.car.dto.CarResponse;
import com.munevver.rabam.car.service.CarService;
import com.munevver.rabam.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public ApiResponse<Page<CarResponse>> getAllCars(Pageable pageable) {
        return ApiResponse.success("Cars listed successfully", carService.getAllCars(pageable));
    }

    @PostMapping
    public ApiResponse<CarResponse> createCar(@Valid @RequestBody CarRequest request) {
        return ApiResponse.success("Car created successfully", carService.createCar(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CarResponse> updateCar(
            @PathVariable Long id,
            @Valid @RequestBody CarRequest request
    ) {
        return ApiResponse.success("Car updated successfully", carService.updateCar(id, request));
    }
}
