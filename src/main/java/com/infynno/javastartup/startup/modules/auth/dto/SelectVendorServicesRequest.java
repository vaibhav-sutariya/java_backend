package com.infynno.javastartup.startup.modules.auth.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SelectVendorServicesRequest {
    @NotEmpty(message = "Service IDs cannot be empty")
    private List<String> serviceIds;
}
