package com.munevver.rabam.service.integration;

import com.munevver.rabam.car.entity.Car;
import com.munevver.rabam.car.repository.CarRepository;
import com.munevver.rabam.service.entity.Service;
import com.munevver.rabam.service.enums.ServiceStatus;
import com.munevver.rabam.service.repository.ServiceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ServiceOptimisticLockingIntegrationTest {

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    private Long serviceId;

    @BeforeEach
    void setUp() {
        serviceRepository.deleteAll();
        carRepository.deleteAll();

        Car car = new Car();
        car.setLicensePlate("TST " + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        car.setBrand("Skoda");
        car.setModel("Octavia");

        Car savedCar = carRepository.save(car);

        Service service = new Service();
        service.setTitle("Oil Change");
        service.setDescription("Initial service");
        service.setStatus(ServiceStatus.PENDING);
        service.setCar(savedCar);

        Service savedService = serviceRepository.save(service);

        this.serviceId = savedService.getId();
    }

    @Test
    void shouldRejectSecondUpdateWhenServiceWasUpdatedByAnotherSession() {
        Service firstSessionCopy = loadServiceInSeparateSession(serviceId);
        Service secondSessionCopy = loadServiceInSeparateSession(serviceId);

        updateServiceInSeparateSession(firstSessionCopy, "Updated by first user");

        assertThrows(
                OptimisticLockException.class,
                () -> updateServiceInSeparateSession(secondSessionCopy, "Updated by second stale user")
        );
    }

    private Service loadServiceInSeparateSession(Long id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Service service = entityManager.find(Service.class, id);

            transaction.commit();

            return service;
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            entityManager.close();
        }
    }

    private void updateServiceInSeparateSession(Service detachedService, String newTitle) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            detachedService.setTitle(newTitle);

            entityManager.merge(detachedService);
            entityManager.flush();

            transaction.commit();
        } finally {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            entityManager.close();
        }
    }
}