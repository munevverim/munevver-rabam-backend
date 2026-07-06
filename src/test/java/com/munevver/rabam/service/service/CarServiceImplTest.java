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
import com.munevver.rabam.event.publisher.DomainEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @Spy
    private CarMapper carMapper = new CarMapper();

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @Mock
    private I18nMessageService messageService;

    @InjectMocks
    private CarServiceImpl carService;

    @BeforeEach
    void setUp() {
        lenient()
                .when(messageService.getMessage(anyString(), any(Object[].class)))
                .thenAnswer(invocation -> invocation.getArgument(0, String.class));
    }

    @Test
    void shouldGetAllCars() {
        Pageable pageable = PageRequest.of(0, 10);

        Car car = buildCar(1L, "34 ABC 123", "Toyota", "Corolla");

        Page<Car> carPage = new PageImpl<>(List.of(car), pageable, 1);

        when(carRepository.findAll(pageable)).thenReturn(carPage);

        Page<CarResponse> response = carService.getAllCars(pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("34 ABC 123", response.getContent().get(0).getLicensePlate());
        assertEquals("Toyota", response.getContent().get(0).getBrand());
        assertEquals("Corolla", response.getContent().get(0).getModel());
    }

    @Test
    void shouldCreateCar() {
        CarRequest request = buildCarRequest("34 abc 123", "Toyota", "Corolla");

        when(carRepository.existsByLicensePlateIgnoreCase("34 ABC 123")).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            car.setId(1L);
            car.setCreatedAt(LocalDateTime.now());
            car.setUpdatedAt(LocalDateTime.now());
            return car;
        });

        CarResponse response = carService.createCar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("34 ABC 123", response.getLicensePlate());
        assertEquals("Toyota", response.getBrand());
        assertEquals("Corolla", response.getModel());

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository).save(carCaptor.capture());

        Car savedCar = carCaptor.getValue();

        assertEquals("34 ABC 123", savedCar.getLicensePlate());
        assertEquals("Toyota", savedCar.getBrand());
        assertEquals("Corolla", savedCar.getModel());

        verify(domainEventPublisher).publish(any(DomainEvent.class));
    }

    @Test
    void shouldThrowConflictExceptionWhenLicensePlateAlreadyExistsOnCreate() {
        CarRequest request = buildCarRequest("34 abc 123", "Toyota", "Corolla");

        when(carRepository.existsByLicensePlateIgnoreCase("34 ABC 123")).thenReturn(true);

        assertThrows(ConflictException.class, () -> carService.createCar(request));

        verify(carRepository, never()).save(any(Car.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    void shouldUpdateCar() {
        Long carId = 1L;

        Car existingCar = buildCar(carId, "34 ABC 123", "Toyota", "Corolla");
        CarRequest request = buildCarRequest("06 def 456", "Honda", "Civic");

        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(carRepository.existsByLicensePlateIgnoreCase("06 DEF 456")).thenReturn(false);
        when(carRepository.save(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CarResponse response = carService.updateCar(carId, request);

        assertNotNull(response);
        assertEquals(carId, response.getId());
        assertEquals("06 DEF 456", response.getLicensePlate());
        assertEquals("Honda", response.getBrand());
        assertEquals("Civic", response.getModel());

        verify(carRepository).save(existingCar);
        verify(domainEventPublisher).publish(any(DomainEvent.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCarDoesNotExistOnUpdate() {
        Long carId = 99L;

        CarRequest request = buildCarRequest("34 ABC 123", "Toyota", "Corolla");

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> carService.updateCar(carId, request));

        verify(carRepository, never()).save(any(Car.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    void shouldThrowConflictExceptionWhenLicensePlateAlreadyExistsOnUpdate() {
        Long carId = 1L;

        Car existingCar = buildCar(carId, "34 ABC 123", "Toyota", "Corolla");
        CarRequest request = buildCarRequest("06 def 456", "Honda", "Civic");

        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(carRepository.existsByLicensePlateIgnoreCase("06 DEF 456")).thenReturn(true);

        assertThrows(ConflictException.class, () -> carService.updateCar(carId, request));

        verify(carRepository, never()).save(any(Car.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    private CarRequest buildCarRequest(String licensePlate, String brand, String model) {
        CarRequest request = new CarRequest();
        request.setLicensePlate(licensePlate);
        request.setBrand(brand);
        request.setModel(model);
        return request;
    }

    private Car buildCar(Long id, String licensePlate, String brand, String model) {
        Car car = new Car();
        car.setId(id);
        car.setLicensePlate(licensePlate);
        car.setBrand(brand);
        car.setModel(model);
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());
        return car;
    }
}