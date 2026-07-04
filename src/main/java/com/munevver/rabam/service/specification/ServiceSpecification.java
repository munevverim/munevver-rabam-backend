package com.munevver.rabam.service.specification;

import com.munevver.rabam.service.enums.ServiceStatus;
import org.springframework.data.jpa.domain.Specification;
import com.munevver.rabam.service.entity.Service;

public class ServiceSpecification {

    private ServiceSpecification() {
    }

    public static Specification<Service> hasCarId(Long carId) {
        return (root, query, cb) -> carId == null
                ? cb.conjunction()
                : cb.equal(root.get("car").get("id"), carId);
    }

    public static Specification<Service> hasStatus(ServiceStatus status) {
        return (root, query, cb) -> status == null
                ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }
}
