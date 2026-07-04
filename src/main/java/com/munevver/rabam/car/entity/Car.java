package com.munevver.rabam.car.entity;

import com.munevver.rabam.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
        name = "cars",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_car_license_plate", columnNames = "license_plate")
        }
)
public class Car extends BaseEntity {

    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;
}
