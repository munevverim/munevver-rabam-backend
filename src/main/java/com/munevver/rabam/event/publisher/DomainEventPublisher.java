package com.munevver.rabam.event.publisher;

import com.munevver.rabam.event.dto.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}