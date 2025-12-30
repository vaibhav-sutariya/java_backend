package com.infynno.javastartup.startup.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddVendorServiceRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotNull(message = "Price cannot be null")
    private int price;
    @NotBlank(message = "Next Service cannot be blank")
    private String nextService;
}
