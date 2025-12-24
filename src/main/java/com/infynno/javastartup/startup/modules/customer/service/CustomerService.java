package com.infynno.javastartup.startup.modules.customer.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.common.response.ApiResponse;
import com.infynno.javastartup.startup.common.response.Pagination;
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
    @Transactional
    public ApiResponse<List<CustomerResponse>> getAllCustomers(String currentUserId, Pageable pageable) throws AuthException {
        Page<Customer> page = customerRepository.findByCreatedById(currentUserId, pageable);

        List<CustomerResponse> data = page.getContent().stream().map(CustomerResponse::fromEntity).collect(Collectors.toList());

        Pagination pagination= new Pagination(page.getNumber(), page.getSize(), page.getTotalElements() , page.getTotalPages());

        return  ApiResponse.success("Customer fetched successfully", data, pagination);
    }
}
