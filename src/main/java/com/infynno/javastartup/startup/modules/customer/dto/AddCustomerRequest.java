package com.infynno.javastartup.startup.modules.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddCustomerRequest {
    @NotBlank
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$",
            message = "Invalid phone number. Must be 10 digits and start with 6-9")
    private String phoneNumber;

    @Size(max = 500, message = "Address too long")
    private String address;

    @Size(max = 100)
    private String state;

    @Size(max = 100)
    private String city;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid ZIP code")
    private String zipCode;

}
