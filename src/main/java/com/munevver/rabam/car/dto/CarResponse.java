package com.munevver.rabam.car.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Builder
public class CarResponse {

    private Long id;
    private String licensePlate;
    private String brand;
    private String model;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
