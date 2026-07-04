package com.munevver.rabam.service.service;

import com.munevver.rabam.common.exception.BadRequestException;
import com.munevver.rabam.service.enums.ServiceStatus;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ServiceStatusTransitionValidator {

    private static final Map<ServiceStatus, ServiceStatus> VALID_NEXT_STATUSES = Map.of(
            ServiceStatus.PENDING, ServiceStatus.IN_PROGRESS,
            ServiceStatus.IN_PROGRESS, ServiceStatus.DONE
    );

    public void validate(ServiceStatus currentStatus, ServiceStatus requestedStatus) {
        if (requestedStatus == null) {
            return;
        }

        ServiceStatus validNextStatus = VALID_NEXT_STATUSES.get(currentStatus);

        if (validNextStatus == null) {
            throw new BadRequestException(
                    "Invalid status transition attempted from " + currentStatus + " to " + requestedStatus +
                            ". Current status has no next valid state."
            );
        }

        if (!validNextStatus.equals(requestedStatus)) {
            throw new BadRequestException(
                    "Invalid status transition attempted from " + currentStatus + " to " + requestedStatus +
                            ". Valid next status is " + validNextStatus + "."
            );
        }
    }
}
