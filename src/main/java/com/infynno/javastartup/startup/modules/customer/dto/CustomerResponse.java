package com.infynno.javastartup.startup.modules.customer.dto;

import com.infynno.javastartup.startup.modules.customer.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerResponse {
    private String id;
    private String name;
    private String phoneNumber;
    private String address;
    private String state;
    private String city;
    private String zipCode;
    private String createdBy;
    private String updatedBy;
    private String createdAt;
    private String updatedAt;

    public CustomerResponse(String id, String name, String phoneNumber, String address,
            String state, String city, String zipCode) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.state = state;
        this.city = city;
        this.zipCode = zipCode;
    }

    public static CustomerResponse fromEntity(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getName(), customer.getPhoneNumber(),
                customer.getAddress(), customer.getState(), customer.getCity(),
                customer.getZipCode());
    }

}
