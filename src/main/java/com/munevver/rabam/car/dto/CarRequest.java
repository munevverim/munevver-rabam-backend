package com.munevver.rabam.car.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CarRequest {

    @NotBlank(message = "License plate is required")
    @Size(max = 20, message = "License plate must be at most 20 characters")
    @Pattern(
            regexp = "^[A-Z0-9]+([ -]?[A-Z0-9]+)*$",
            message = "License plate format is invalid. Only uppercase letters, numbers, spaces and hyphens are allowed"
    )
    private String licensePlate;

    @NotBlank(message = "Brand is required")
    @Size(max = 100, message = "Brand must be at most 100 characters")
    private String brand;

    @NotBlank(message = "Model is required")
    @Size(max = 100, message = "Model must be at most 100 characters")
    private String model;
}
