package com.infynno.javastartup.startup.modules.customer.service;

import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.customer.dto.AddCustomerRequest;
import com.infynno.javastartup.startup.modules.customer.dto.CustomerResponse;
import com.infynno.javastartup.startup.modules.customer.model.Customer;
import com.infynno.javastartup.startup.modules.customer.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Transactional
    public CustomerResponse addCustomer(AddCustomerRequest req, User currentUserId)
            throws AuthException {

        Customer customer = Customer.builder().name(req.getName()).phoneNumber(req.getPhoneNumber())
                .address(req.getAddress()).state(req.getState()).city(req.getCity())
                .zipCode(req.getZipCode()).createdBy(currentUserId).updatedBy(currentUserId)
                .build();

        customerRepository.save(customer);

        return CustomerResponse.fromEntity(customer);

    }
}
