package com.infynno.javastartup.startup.modules.customer.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.customer.dto.AddCustomerRequest;
import com.infynno.javastartup.startup.modules.customer.dto.CustomerResponse;
import com.infynno.javastartup.startup.modules.customer.repository.CustomerRepository;
import com.infynno.javastartup.startup.modules.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @PostMapping("/add-customer")
    public ResponseEntity<CustomerResponse> addCustomer(@Valid @RequestBody AddCustomerRequest req,
            Authentication authentication) throws AuthException {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(customerService.addCustomer(req, currentUser));
    }


}
