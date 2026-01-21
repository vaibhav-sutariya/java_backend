package com.infynno.javastartup.startup.modules.customer.mapper;

import org.springframework.stereotype.Component;
import com.infynno.javastartup.startup.modules.customer.dto.CustomerResponse;
import com.infynno.javastartup.startup.modules.customer.model.Customer;

@Component
public class CustomerMapper {

    public CustomerResponse toResponse(Customer customer) {
        if (customer == null) {
            return null;
        }

        return new CustomerResponse(customer.getId(), customer.getName(), customer.getPhoneNumber(),
                customer.getAddress(), customer.getState(), customer.getCity(),
                customer.getZipCode(),
                customer.getCreatedBy() != null ? customer.getCreatedBy().getId() : null,
                customer.getUpdatedBy() != null ? customer.getUpdatedBy().getId() : null,
                customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : null,
                customer.getUpdatedAt() != null ? customer.getUpdatedAt().toString() : null);
    }
}
