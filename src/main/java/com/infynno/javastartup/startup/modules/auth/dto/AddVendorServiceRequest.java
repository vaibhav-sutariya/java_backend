package com.infynno.javastartup.startup.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddVendorServiceRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Price cannot be blank")
    private int price;
    @NotBlank(message = "Next Service cannot be blank")
    private String nextService;
}
