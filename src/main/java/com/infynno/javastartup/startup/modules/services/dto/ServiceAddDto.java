package com.infynno.javastartup.startup.modules.services.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceAddDto {
    @NotBlank(message = "Service name is required")
    private String name;

    private String icon;

    private int price;

    private String nextService;
}
