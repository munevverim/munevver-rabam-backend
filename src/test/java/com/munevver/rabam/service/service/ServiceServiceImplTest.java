package com.munevver.rabam.service.service;

import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.common.exception.BadRequestException;
import com.munevver.rabam.common.exception.ConflictException;
import com.munevver.rabam.common.exception.ResourceNotFoundException;
import com.munevver.rabam.event.publisher.DomainEventPublisher;
import com.munevver.rabam.service.dto.ServiceRequest;
import com.munevver.rabam.service.dto.ServiceResponse;
import com.munevver.rabam.service.dto.ServiceUpdateRequest;
import com.munevver.rabam.service.entity.Service;
import com.munevver.rabam.service.enums.ServiceStatus;
import com.munevver.rabam.service.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceImplTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private ServiceStatusTransitionValidator statusTransitionValidator;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    @InjectMocks
    private ServiceServiceImpl serviceService;

    private Car car;
    private Service service;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(5L);
        car.setLicensePlate("06 RBM 205");
        car.setBrand("Skoda");
        car.setModel("Octavia");
        car.setCreatedAt(LocalDateTime.now());

        service = new Service();
        service.setId(2L);
        service.setTitle("Oil Change");
        service.setDescription("Engine oil and filter replacement");
        service.setStatus(ServiceStatus.PENDING);
        service.setCar(car);
        service.setVersion(0L);
        service.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateServiceSuccessfully() {
        ServiceRequest request = new ServiceRequest();
        request.setTitle("Oil Change");
        request.setDescription("Engine oil and filter replacement");
        request.setCarId(5L);

        when(carRepository.findById(5L))
                .thenReturn(Optional.of(car));

        when(serviceRepository.save(any(Service.class)))
                .thenAnswer(invocation -> {
                    Service savedService = invocation.getArgument(0);
                    savedService.setId(2L);
                    savedService.setVersion(0L);
                    savedService.setCreatedAt(LocalDateTime.now());
                    return savedService;
                });

        ServiceResponse response = serviceService.createService(request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("Oil Change", response.getTitle());
        assertEquals("Engine oil and filter replacement", response.getDescription());
        assertEquals(ServiceStatus.PENDING, response.getStatus());
        assertEquals(5L, response.getCarId());
        assertEquals("06 RBM 205", response.getCarLicensePlate());
        assertEquals(0L, response.getVersion());

        verify(carRepository).findById(5L);
        verify(serviceRepository).save(any(Service.class));
        verify(domainEventPublisher).publish(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCarDoesNotExistOnCreate() {
        ServiceRequest request = new ServiceRequest();
        request.setTitle("Oil Change");
        request.setDescription("Engine oil and filter replacement");
        request.setCarId(999L);

        when(carRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> serviceService.createService(request)
        );

        assertTrue(exception.getMessage().contains("Car not found"));

        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    void shouldUpdateServiceStatusSuccessfully() {
        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setStatus(ServiceStatus.IN_PROGRESS);
        request.setVersion(0L);

        when(serviceRepository.findById(2L))
                .thenReturn(Optional.of(service));

        when(carRepository.findByIdForUpdate(5L))
                .thenReturn(Optional.of(car));

        when(serviceRepository.findByCarIdAndStatusForUpdate(5L, ServiceStatus.IN_PROGRESS))
                .thenReturn(List.of());

        when(serviceRepository.save(any(Service.class)))
                .thenAnswer(invocation -> {
                    Service updatedService = invocation.getArgument(0);
                    updatedService.setVersion(1L);
                    updatedService.setUpdatedAt(LocalDateTime.now());
                    return updatedService;
                });

        ServiceResponse response = serviceService.updateService(2L, request);

        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals(ServiceStatus.IN_PROGRESS, response.getStatus());
        assertEquals(1L, response.getVersion());

        verify(statusTransitionValidator)
                .validate(ServiceStatus.PENDING, ServiceStatus.IN_PROGRESS);

        verify(carRepository).findByIdForUpdate(5L);
        verify(serviceRepository).findByCarIdAndStatusForUpdate(5L, ServiceStatus.IN_PROGRESS);
        verify(serviceRepository).save(any(Service.class));
        verify(domainEventPublisher).publish(any());
    }

    @Test
    void shouldUpdateTitleAndDescriptionSuccessfully() {
        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setTitle("Inspection");
        request.setDescription("General vehicle inspection");
        request.setVersion(0L);

        when(serviceRepository.findById(2L))
                .thenReturn(Optional.of(service));

        when(serviceRepository.save(any(Service.class)))
                .thenAnswer(invocation -> {
                    Service updatedService = invocation.getArgument(0);
                    updatedService.setVersion(1L);
                    updatedService.setUpdatedAt(LocalDateTime.now());
                    return updatedService;
                });

        ServiceResponse response = serviceService.updateService(2L, request);

        assertNotNull(response);
        assertEquals("Inspection", response.getTitle());
        assertEquals("General vehicle inspection", response.getDescription());
        assertEquals(ServiceStatus.PENDING, response.getStatus());
        assertEquals(1L, response.getVersion());

        verify(statusTransitionValidator, never()).validate(any(), any());
        verify(carRepository, never()).findByIdForUpdate(any());
        verify(serviceRepository, never()).findByCarIdAndStatusForUpdate(any(), any());
        verify(serviceRepository).save(any(Service.class));
        verify(domainEventPublisher).publish(any());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenServiceDoesNotExistOnUpdate() {
        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setStatus(ServiceStatus.IN_PROGRESS);
        request.setVersion(0L);

        when(serviceRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> serviceService.updateService(999L, request)
        );

        assertTrue(exception.getMessage().contains("Service not found"));

        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowConflictExceptionWhenVersionDoesNotMatch() {
        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setStatus(ServiceStatus.IN_PROGRESS);
        request.setVersion(99L);

        when(serviceRepository.findById(2L))
                .thenReturn(Optional.of(service));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> serviceService.updateService(2L, request)
        );

        assertTrue(exception.getMessage().contains("updated by another user"));

        verify(statusTransitionValidator, never()).validate(any(), any());
        verify(carRepository, never()).findByIdForUpdate(any());
        verify(serviceRepository, never()).findByCarIdAndStatusForUpdate(any(), any());
        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenStatusTransitionIsInvalid() {
        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setStatus(ServiceStatus.DONE);
        request.setVersion(0L);

        when(serviceRepository.findById(2L))
                .thenReturn(Optional.of(service));

        doThrow(new BadRequestException("Invalid status transition attempted from PENDING to DONE"))
                .when(statusTransitionValidator)
                .validate(ServiceStatus.PENDING, ServiceStatus.DONE);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> serviceService.updateService(2L, request)
        );

        assertTrue(exception.getMessage().contains("Invalid status transition"));

        verify(carRepository, never()).findByIdForUpdate(any());
        verify(serviceRepository, never()).findByCarIdAndStatusForUpdate(any(), any());
        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any());
    }

    @Test
    void shouldThrowBadRequestExceptionWhenMaxActiveServicesLimitExceeded() {
        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setStatus(ServiceStatus.IN_PROGRESS);
        request.setVersion(0L);

        Service activeService1 = new Service();
        activeService1.setId(10L);
        activeService1.setStatus(ServiceStatus.IN_PROGRESS);
        activeService1.setCar(car);

        Service activeService2 = new Service();
        activeService2.setId(11L);
        activeService2.setStatus(ServiceStatus.IN_PROGRESS);
        activeService2.setCar(car);

        when(serviceRepository.findById(2L))
                .thenReturn(Optional.of(service));

        when(carRepository.findByIdForUpdate(5L))
                .thenReturn(Optional.of(car));

        when(serviceRepository.findByCarIdAndStatusForUpdate(5L, ServiceStatus.IN_PROGRESS))
                .thenReturn(List.of(activeService1, activeService2));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> serviceService.updateService(2L, request)
        );

        assertTrue(exception.getMessage().contains("at most 2 services in progress"));

        verify(statusTransitionValidator)
                .validate(ServiceStatus.PENDING, ServiceStatus.IN_PROGRESS);

        verify(carRepository).findByIdForUpdate(5L);
        verify(serviceRepository).findByCarIdAndStatusForUpdate(5L, ServiceStatus.IN_PROGRESS);
        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any());
    }
}