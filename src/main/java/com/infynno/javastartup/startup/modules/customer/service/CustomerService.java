package com.infynno.javastartup.startup.modules.customer.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.modules.customer.dto.AddCustomerRequest;
import com.infynno.javastartup.startup.modules.customer.dto.CustomerResponse;
import com.infynno.javastartup.startup.modules.customer.mapper.CustomerMapper;
import com.infynno.javastartup.startup.modules.customer.model.Customer;
import com.infynno.javastartup.startup.modules.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    @CacheEvict(value = "customers", allEntries = true)
    public CustomerResponse addCustomer(AddCustomerRequest req) {
        Customer customer = Customer.builder().name(req.getName()).phoneNumber(req.getPhoneNumber())
                .address(req.getAddress()).state(req.getState()).city(req.getCity())
                .zipCode(req.getZipCode()).build();
        // Auditing handles createdBy/updatedBy/dates
        customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    public CustomerResponse getCustomerById(String customerId) throws AuthException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AuthException("Customer not found"));
        return customerMapper.toResponse(customer);
    }

    @Cacheable(value = "customers",
            key = "#userId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<CustomerResponse> getAllCustomers(String userId, Pageable pageable) {
        // Assuming filtering by creator is required logic
        Page<Customer> page = customerRepository.findByCreatedById(userId, pageable);
        return page.map(customerMapper::toResponse);
    }

    @Transactional
    @CacheEvict(value = "customers", allEntries = true)
    public CustomerResponse updateCustomer(String customerId, AddCustomerRequest req)
            throws AuthException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AuthException("Customer not found"));

        customer.setName(req.getName());
        customer.setPhoneNumber(req.getPhoneNumber());
        customer.setAddress(req.getAddress());
        customer.setState(req.getState());
        customer.setCity(req.getCity());
        customer.setZipCode(req.getZipCode());
        // Auditing handles updatedBy/updatedAt

        customerRepository.save(customer);
        return customerMapper.toResponse(customer);
    }

    @Transactional
    @CacheEvict(value = "customers", allEntries = true)
    public void deleteCustomer(String customerId) throws AuthException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new AuthException("Customer not found"));
        customerRepository.delete(customer);
    }
}
