package com.munevver.rabam.service.entity;

import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.common.entity.BaseEntity;
import com.munevver.rabam.service.enums.ServiceStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="services")
public class Service extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceStatus status = ServiceStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Version
    private Long version;
}
