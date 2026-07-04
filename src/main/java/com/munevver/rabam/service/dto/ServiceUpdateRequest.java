package com.munevver.rabam.service.dto;

import com.munevver.rabam.service.enums.ServiceStatus;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceUpdateRequest {

    @Size(max = 150, message = "Title must be at most 150 characters")
    private String title;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    private ServiceStatus status;

    private Long version;
}
