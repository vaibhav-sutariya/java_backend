package com.infynno.javastartup.startup.modules.customer.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    @Autowired
    private final CustomerRepository customerRepository;

    @CacheEvict(value = "customers", allEntries = true)
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
    public ApiResponse<CustomerResponse> getCustomerById(String customerId) throws AuthException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AuthException("Customer not found"));

        return ApiResponse.success("Customer fetched successfully", CustomerResponse.fromEntity(customer));
    }


 
    @Cacheable(value = "customers",
    key = "#currentUserId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort"
)
    @Transactional
    public ApiResponse<List<CustomerResponse>> getAllCustomers(String currentUserId, Pageable pageable) throws AuthException {
        Page<Customer> page = customerRepository.findByCreatedById(currentUserId, pageable);

        List<CustomerResponse> data = page.getContent().stream().map(CustomerResponse::fromEntity).collect(Collectors.toList());

        Pagination pagination= new Pagination(page.getNumber(), page.getSize(), page.getTotalElements() , page.getTotalPages());

        return  ApiResponse.success("Customer fetched successfully", data, pagination);
    }

    @CacheEvict(value = "customers", allEntries = true)
    @Transactional
    public ApiResponse<CustomerResponse> updateCustomer(String customerId, AddCustomerRequest req, User currentUser)
            throws AuthException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AuthException("Customer not found"));

        customer.setName(req.getName());
        customer.setPhoneNumber(req.getPhoneNumber());
        customer.setAddress(req.getAddress());
        customer.setState(req.getState());
        customer.setCity(req.getCity());
        customer.setZipCode(req.getZipCode());
        customer.setUpdatedBy(currentUser);

        customerRepository.save(customer);

        return ApiResponse.success("Customer updated successfully", CustomerResponse.fromEntity(customer));
    }

    @CacheEvict(value = "customers", allEntries = true)
    public ApiResponse<Void> deleteCustomer(String customerId) throws AuthException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AuthException("Customer not found"));

        customerRepository.delete(customer);

        return ApiResponse.success("Customer deleted successfully", null);
    }
}
