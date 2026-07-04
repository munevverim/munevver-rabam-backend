package com.munevver.rabam.service.service;

import com.munevver.rabam.service.dto.ServiceRequest;
import com.munevver.rabam.service.dto.ServiceResponse;
import com.munevver.rabam.service.dto.ServiceUpdateRequest;
import com.munevver.rabam.service.enums.ServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ServiceService {

    Page<ServiceResponse> getAllServices(Long carId, ServiceStatus status, Pageable pageable);

    ServiceResponse getServiceById(Long id);

    ServiceResponse createService(ServiceRequest request);

    ServiceResponse updateService(Long id, ServiceUpdateRequest request);
}
