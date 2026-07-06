package com.munevver.rabam.service.service;

import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.common.exception.BadRequestException;
import com.munevver.rabam.common.exception.ConflictException;
import com.munevver.rabam.common.exception.ResourceNotFoundException;
import com.munevver.rabam.common.i18n.I18nMessageService;
import com.munevver.rabam.event.dto.DomainEvent;
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
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Mock
    private I18nMessageService messageService;

    @InjectMocks
    private ServiceServiceImpl serviceService;

    @BeforeEach
    void setUp() {
        lenient()
                .when(messageService.getMessage(anyString(), any(Object[].class)))
                .thenAnswer(invocation -> invocation.getArgument(0, String.class));
    }

    @Test
    void shouldGetAllServices() {
        Pageable pageable = PageRequest.of(0, 10);

        Car car = buildCar(1L);
        Service service = buildService(1L, car, ServiceStatus.PENDING, 0L);

        Page<Service> servicePage = new PageImpl<>(List.of(service), pageable, 1);

        when(serviceRepository.findAll(
                ArgumentMatchers.<Specification<Service>>any(),
                eq(pageable)
        )).thenReturn(servicePage);

        Page<ServiceResponse> response = serviceService.getAllServices(null, null, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("Yağ Değişimi", response.getContent().get(0).getTitle());
        assertEquals(ServiceStatus.PENDING, response.getContent().get(0).getStatus());
        assertEquals(car.getId(), response.getContent().get(0).getCarId());
    }

    @Test
    void shouldGetServiceById() {
        Long serviceId = 1L;

        Car car = buildCar(1L);
        Service service = buildService(serviceId, car, ServiceStatus.PENDING, 0L);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        ServiceResponse response = serviceService.getServiceById(serviceId);

        assertNotNull(response);
        assertEquals(serviceId, response.getId());
        assertEquals("Yağ Değişimi", response.getTitle());
        assertEquals(ServiceStatus.PENDING, response.getStatus());
        assertEquals(car.getId(), response.getCarId());
    }

    @Test
    void shouldCreateService() {
        Long carId = 1L;

        Car car = buildCar(carId);
        ServiceRequest request = buildServiceRequest(carId, "Yağ Değişimi", "Motor yağı değiştirilecek.");

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> {
            Service service = invocation.getArgument(0);
            service.setId(1L);
            service.setVersion(0L);
            return service;
        });

        ServiceResponse response = serviceService.createService(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Yağ Değişimi", response.getTitle());
        assertEquals("Motor yağı değiştirilecek.", response.getDescription());
        assertEquals(ServiceStatus.PENDING, response.getStatus());
        assertEquals(carId, response.getCarId());

        verify(serviceRepository).save(any(Service.class));
        verify(domainEventPublisher).publish(any(DomainEvent.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenCarDoesNotExistOnCreate() {
        Long carId = 99L;

        ServiceRequest request = buildServiceRequest(carId, "Yağ Değişimi", "Motor yağı değiştirilecek.");

        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceService.createService(request));

        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    void shouldUpdateServiceTitleAndDescription() {
        Long serviceId = 1L;

        Car car = buildCar(1L);
        Service service = buildService(serviceId, car, ServiceStatus.PENDING, 0L);

        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setTitle("Fren Bakımı");
        request.setDescription("Fren diskleri kontrol edilecek.");
        request.setVersion(0L);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = serviceService.updateService(serviceId, request);

        assertNotNull(response);
        assertEquals(serviceId, response.getId());
        assertEquals("Fren Bakımı", response.getTitle());
        assertEquals("Fren diskleri kontrol edilecek.", response.getDescription());
        assertEquals(ServiceStatus.PENDING, response.getStatus());

        verify(serviceRepository).save(service);
        verify(domainEventPublisher).publish(any(DomainEvent.class));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenServiceDoesNotExistOnUpdate() {
        Long serviceId = 99L;

        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setTitle("Fren Bakımı");
        request.setVersion(0L);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> serviceService.updateService(serviceId, request));

        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    void shouldThrowConflictExceptionWhenVersionDoesNotMatch() {
        Long serviceId = 1L;

        Car car = buildCar(1L);
        Service service = buildService(serviceId, car, ServiceStatus.PENDING, 2L);

        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setTitle("Fren Bakımı");
        request.setVersion(1L);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));

        assertThrows(ConflictException.class, () -> serviceService.updateService(serviceId, request));

        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    @Test
    void shouldUpdateServiceStatusToInProgress() {
        Long serviceId = 1L;
        Long carId = 1L;

        Car car = buildCar(carId);
        Service service = buildService(serviceId, car, ServiceStatus.PENDING, 0L);

        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setStatus(ServiceStatus.IN_PROGRESS);
        request.setVersion(0L);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(carRepository.findByIdForUpdate(carId)).thenReturn(Optional.of(car));
        when(serviceRepository.findByCarIdAndStatusForUpdate(carId, ServiceStatus.IN_PROGRESS))
                .thenReturn(List.of());
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = serviceService.updateService(serviceId, request);

        assertNotNull(response);
        assertEquals(serviceId, response.getId());
        assertEquals(ServiceStatus.IN_PROGRESS, response.getStatus());

        verify(statusTransitionValidator).validate(ServiceStatus.PENDING, ServiceStatus.IN_PROGRESS);
        verify(serviceRepository).save(service);
        verify(domainEventPublisher).publish(any(DomainEvent.class));
    }

    @Test
    void shouldUpdateServiceStatusToDone() {
        Long serviceId = 1L;

        Car car = buildCar(1L);
        Service service = buildService(serviceId, car, ServiceStatus.IN_PROGRESS, 0L);

        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setStatus(ServiceStatus.DONE);
        request.setVersion(0L);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(serviceRepository.save(any(Service.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ServiceResponse response = serviceService.updateService(serviceId, request);

        assertNotNull(response);
        assertEquals(serviceId, response.getId());
        assertEquals(ServiceStatus.DONE, response.getStatus());

        verify(statusTransitionValidator).validate(ServiceStatus.IN_PROGRESS, ServiceStatus.DONE);
        verify(serviceRepository).save(service);
        verify(domainEventPublisher).publish(any(DomainEvent.class));
    }

    @Test
    void shouldThrowBadRequestExceptionWhenMaxActiveServicesLimitExceeded() {
        Long serviceId = 1L;
        Long carId = 1L;

        Car car = buildCar(carId);
        Service service = buildService(serviceId, car, ServiceStatus.PENDING, 0L);

        Service activeService1 = buildService(2L, car, ServiceStatus.IN_PROGRESS, 0L);
        Service activeService2 = buildService(3L, car, ServiceStatus.IN_PROGRESS, 0L);

        ServiceUpdateRequest request = new ServiceUpdateRequest();
        request.setStatus(ServiceStatus.IN_PROGRESS);
        request.setVersion(0L);

        when(serviceRepository.findById(serviceId)).thenReturn(Optional.of(service));
        when(carRepository.findByIdForUpdate(carId)).thenReturn(Optional.of(car));
        when(serviceRepository.findByCarIdAndStatusForUpdate(carId, ServiceStatus.IN_PROGRESS))
                .thenReturn(List.of(activeService1, activeService2));

        assertThrows(BadRequestException.class, () -> serviceService.updateService(serviceId, request));

        verify(statusTransitionValidator).validate(ServiceStatus.PENDING, ServiceStatus.IN_PROGRESS);
        verify(serviceRepository, never()).save(any(Service.class));
        verify(domainEventPublisher, never()).publish(any(DomainEvent.class));
    }

    private ServiceRequest buildServiceRequest(Long carId, String title, String description) {
        ServiceRequest request = new ServiceRequest();
        request.setCarId(carId);
        request.setTitle(title);
        request.setDescription(description);
        return request;
    }

    private Car buildCar(Long id) {
        Car car = new Car();
        car.setId(id);
        car.setLicensePlate("34 ABC 123");
        car.setBrand("Toyota");
        car.setModel("Corolla");
        car.setCreatedAt(LocalDateTime.now());
        car.setUpdatedAt(LocalDateTime.now());
        return car;
    }

    private Service buildService(Long id, Car car, ServiceStatus status, Long version) {
        Service service = new Service();
        service.setId(id);
        service.setTitle("Yağ Değişimi");
        service.setDescription("Motor yağı değiştirilecek.");
        service.setStatus(status);
        service.setCar(car);
        service.setVersion(version);
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        return service;
    }
}