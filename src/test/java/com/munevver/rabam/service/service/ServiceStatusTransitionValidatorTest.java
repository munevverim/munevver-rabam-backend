package com.munevver.rabam.service.service;

import com.munevver.rabam.common.exception.BadRequestException;
import com.munevver.rabam.service.enums.ServiceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceStatusTransitionValidatorTest {

    private ServiceStatusTransitionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ServiceStatusTransitionValidator();
    }

    @Test
    void shouldAllowPendingToInProgress() {
        assertDoesNotThrow(() ->
                validator.validate(ServiceStatus.PENDING, ServiceStatus.IN_PROGRESS)
        );
    }

    @Test
    void shouldAllowInProgressToDone() {
        assertDoesNotThrow(() ->
                validator.validate(ServiceStatus.IN_PROGRESS, ServiceStatus.DONE)
        );
    }

    @Test
    void shouldAllowNullRequestedStatus() {
        assertDoesNotThrow(() ->
                validator.validate(ServiceStatus.PENDING, null)
        );
    }

    @Test
    void shouldRejectPendingToDoneBecauseSkippingIsNotAllowed() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> validator.validate(ServiceStatus.PENDING, ServiceStatus.DONE)
        );

        assertTrue(exception.getMessage().contains("Invalid status transition"));
        assertTrue(exception.getMessage().contains("PENDING"));
        assertTrue(exception.getMessage().contains("DONE"));
    }

    @Test
    void shouldRejectInProgressToPendingBecauseBackwardTransitionIsNotAllowed() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> validator.validate(ServiceStatus.IN_PROGRESS, ServiceStatus.PENDING)
        );

        assertTrue(exception.getMessage().contains("Invalid status transition"));
        assertTrue(exception.getMessage().contains("IN_PROGRESS"));
        assertTrue(exception.getMessage().contains("PENDING"));
    }

    @Test
    void shouldRejectDoneToInProgressBecauseDoneHasNoNextState() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> validator.validate(ServiceStatus.DONE, ServiceStatus.IN_PROGRESS)
        );

        assertTrue(exception.getMessage().contains("Invalid status transition"));
        assertTrue(exception.getMessage().contains("DONE"));
        assertTrue(exception.getMessage().contains("IN_PROGRESS"));
    }

    @Test
    void shouldRejectReEnteringSameState() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> validator.validate(ServiceStatus.PENDING, ServiceStatus.PENDING)
        );

        assertTrue(exception.getMessage().contains("Invalid status transition"));
    }
}