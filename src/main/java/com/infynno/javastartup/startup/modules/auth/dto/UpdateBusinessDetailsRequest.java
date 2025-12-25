package com.infynno.javastartup.startup.modules.auth.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBusinessDetailsRequest {
    @NotBlank(message = "Business Name is required")
    private String businessName;

    @Pattern(regexp = "\\d{10}")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Business Address is required")
    private String businessAddress;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Zip Code is required")
    @Pattern(regexp = "\\d{5,6}")
    private String zipCode;

    @Pattern(regexp = "[0-9A-Z]{15}")
    private String gstNumber;

    private String notes;


    @DecimalMin("0.0")
    @DecimalMax("100.0")
    private BigDecimal gstPercentage;

}
