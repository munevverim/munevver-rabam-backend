package com.munevver.rabam.car.service;

import com.munevver.rabam.car.dto.CarRequest;
import com.munevver.rabam.car.dto.CarResponse;
import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.common.exception.ConflictException;
import com.munevver.rabam.common.exception.ResourceNotFoundException;
import com.munevver.rabam.event.publisher.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private CarServiceImpl carService;

    private CarRequest request;
    private Car car;

    @BeforeEach
    void setUp() {
        request = new CarRequest();
        request.setLicensePlate("34 abc 123");
        request.setBrand("Toyota");
        request.setModel("Corolla");

        car = new Car();
        car.setId(1L);
        car.setLicensePlate("34 ABC 123");
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateCarSuccessfully() {
        when(carRepository.existsByLicensePlateIgnoreCase("34 ABC 123"))
                .thenReturn(false);

        when(carRepository.save(any(Car.class)))
                .thenReturn(car);

        CarResponse response = carService.createCar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("34 ABC 123", response.getLicensePlate());
        assertEquals("Toyota", response.getBrand());
        assertEquals("Corolla", response.getModel());

        verify(carRepository).save(any(Car.class));
        verify(domainEventPublisher).publish(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenLicensePlateAlreadyExistsOnCreate() {
        when(carRepository.existsByLicensePlateIgnoreCase("34 ABC 123"))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> carService.createCar(request)
        );

        assertTrue(exception.getMessage().contains("License plate already exists"));

        verify(carRepository, never()).save(any(Car.class));
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    void shouldUpdateCarSuccessfully() {
        CarRequest updateRequest = new CarRequest();
        updateRequest.setLicensePlate("06 RBM 205");
        updateRequest.setBrand("Skoda");
        updateRequest.setModel("Octavia");

        Car existingCar = new Car();
        existingCar.setId(1L);
        existingCar.setLicensePlate("34 ABC 123");
        existingCar.setBrand("Toyota");
        existingCar.setModel("Corolla");
        existingCar.setCreatedAt(LocalDateTime.now());

        Car updatedCar = new Car();
        updatedCar.setId(1L);
        updatedCar.setLicensePlate("06 RBM 205");
        updatedCar.setBrand("Skoda");
        updatedCar.setModel("Octavia");
        updatedCar.setCreatedAt(existingCar.getCreatedAt());
        updatedCar.setUpdatedAt(LocalDateTime.now());

        when(carRepository.findById(1L))
                .thenReturn(Optional.of(existingCar));

        when(carRepository.existsByLicensePlateIgnoreCase("06 RBM 205"))
                .thenReturn(false);

        when(carRepository.save(any(Car.class)))
                .thenReturn(updatedCar);

        CarResponse response = carService.updateCar(1L, updateRequest);

        assertNotNull(response);
        assertEquals("06 RBM 205", response.getLicensePlate());
        assertEquals("Skoda", response.getBrand());
        assertEquals("Octavia", response.getModel());

        verify(carRepository).save(any(Car.class));
        verify(domainEventPublisher).publish(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCarDoesNotExistOnUpdate() {
        when(carRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> carService.updateCar(999L, request)
        );

        assertTrue(exception.getMessage().contains("Car not found"));

        verify(carRepository, never()).save(any(Car.class));
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenLicensePlateAlreadyExistsOnUpdate() {
        Car existingCar = new Car();
        existingCar.setId(1L);
        existingCar.setLicensePlate("34 OLD 123");
        existingCar.setBrand("Toyota");
        existingCar.setModel("Corolla");

        CarRequest updateRequest = new CarRequest();
        updateRequest.setLicensePlate("06 RBM 205");
        updateRequest.setBrand("Skoda");
        updateRequest.setModel("Octavia");

        when(carRepository.findById(1L))
                .thenReturn(Optional.of(existingCar));

        when(carRepository.existsByLicensePlateIgnoreCase("06 RBM 205"))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> carService.updateCar(1L, updateRequest)
        );

        assertTrue(exception.getMessage().contains("License plate already exists"));

        verify(carRepository, never()).save(any(Car.class));
        verify(domainEventPublisher, never()).publish(any());
    }
}