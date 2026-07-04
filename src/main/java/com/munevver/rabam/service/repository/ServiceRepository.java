package com.munevver.rabam.service.repository;

import com.munevver.rabam.service.entity.Service;
import com.munevver.rabam.service.enums.ServiceStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long>, JpaSpecificationExecutor<Service> {

    long countByCarIdAndStatus(Long carId, ServiceStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Service s where s.car.id = :carId and s.status = :status")
    List<Service> findByCarIdAndStatusForUpdate(
            @Param("carId") Long carId,
            @Param("status") ServiceStatus status
    );
}