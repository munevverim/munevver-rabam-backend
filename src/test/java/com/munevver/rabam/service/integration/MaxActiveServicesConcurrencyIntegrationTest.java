package com.munevver.rabam.service.integration;

import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.common.exception.BadRequestException;
import com.munevver.rabam.service.dto.ServiceUpdateRequest;
import com.munevver.rabam.service.entity.Service;
import com.munevver.rabam.service.enums.ServiceStatus;
import com.munevver.rabam.service.repository.ServiceRepository;
import com.munevver.rabam.service.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MaxActiveServicesConcurrencyIntegrationTest {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CarRepository carRepository;

    private Long carId;
    private Long firstPendingServiceId;
    private Long secondPendingServiceId;
    private Long firstPendingServiceVersion;
    private Long secondPendingServiceVersion;

    @BeforeEach
    void setUp() {
        serviceRepository.deleteAll();
        carRepository.deleteAll();

        Car car = new Car();
        car.setLicensePlate("CNCR " + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        car.setBrand("Skoda");
        car.setModel("Octavia");

        Car savedCar = carRepository.saveAndFlush(car);
        this.carId = savedCar.getId();

        Service alreadyActiveService = new Service();
        alreadyActiveService.setTitle("Already Active Service");
        alreadyActiveService.setDescription("This service is already in progress");
        alreadyActiveService.setStatus(ServiceStatus.IN_PROGRESS);
        alreadyActiveService.setCar(savedCar);
        serviceRepository.saveAndFlush(alreadyActiveService);

        Service firstPendingService = new Service();
        firstPendingService.setTitle("First Pending Service");
        firstPendingService.setDescription("First concurrent candidate");
        firstPendingService.setStatus(ServiceStatus.PENDING);
        firstPendingService.setCar(savedCar);
        Service savedFirstPendingService = serviceRepository.saveAndFlush(firstPendingService);

        Service secondPendingService = new Service();
        secondPendingService.setTitle("Second Pending Service");
        secondPendingService.setDescription("Second concurrent candidate");
        secondPendingService.setStatus(ServiceStatus.PENDING);
        secondPendingService.setCar(savedCar);
        Service savedSecondPendingService = serviceRepository.saveAndFlush(secondPendingService);

        this.firstPendingServiceId = savedFirstPendingService.getId();
        this.secondPendingServiceId = savedSecondPendingService.getId();
        this.firstPendingServiceVersion = savedFirstPendingService.getVersion();
        this.secondPendingServiceVersion = savedSecondPendingService.getVersion();
    }

    @Test
    void shouldAllowOnlyOneOfTwoConcurrentRequestsWhenOneServiceIsAlreadyInProgress() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        CountDownLatch readyLatch = new CountDownLatch(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Callable<String> firstTask = createMoveToInProgressTask(
                firstPendingServiceId,
                firstPendingServiceVersion,
                readyLatch,
                startLatch
        );

        Callable<String> secondTask = createMoveToInProgressTask(
                secondPendingServiceId,
                secondPendingServiceVersion,
                readyLatch,
                startLatch
        );

        Future<String> firstResult = executorService.submit(firstTask);
        Future<String> secondResult = executorService.submit(secondTask);

        assertTrue(readyLatch.await(5, TimeUnit.SECONDS));

        startLatch.countDown();

        String firstOutcome = firstResult.get(10, TimeUnit.SECONDS);
        String secondOutcome = secondResult.get(10, TimeUnit.SECONDS);

        executorService.shutdown();

        long successCount = java.util.stream.Stream.of(firstOutcome, secondOutcome)
                .filter("SUCCESS"::equals)
                .count();

        long businessFailureCount = java.util.stream.Stream.of(firstOutcome, secondOutcome)
                .filter("BUSINESS_FAILURE"::equals)
                .count();

        long activeServiceCount = serviceRepository.countByCarIdAndStatus(carId, ServiceStatus.IN_PROGRESS);

        assertEquals(1, successCount);
        assertEquals(1, businessFailureCount);
        assertEquals(2, activeServiceCount);
    }

    private Callable<String> createMoveToInProgressTask(
            Long serviceId,
            Long version,
            CountDownLatch readyLatch,
            CountDownLatch startLatch
    ) {
        return () -> {
            readyLatch.countDown();
            startLatch.await();

            ServiceUpdateRequest request = new ServiceUpdateRequest();
            request.setStatus(ServiceStatus.IN_PROGRESS);
            request.setVersion(version);

            try {
                serviceService.updateService(serviceId, request);
                return "SUCCESS";
            } catch (BadRequestException exception) {
                return "BUSINESS_FAILURE";
            }
        };
    }
}