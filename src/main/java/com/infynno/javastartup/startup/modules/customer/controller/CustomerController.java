package com.infynno.javastartup.startup.modules.customer.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.common.response.ApiResponse;
import com.infynno.javastartup.startup.common.response.Pagination;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.customer.dto.AddCustomerRequest;
import com.infynno.javastartup.startup.modules.customer.dto.CustomerResponse;
import com.infynno.javastartup.startup.modules.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/add-customer")
    public ResponseEntity<ApiResponse<CustomerResponse>> addCustomer(
            @Valid @RequestBody AddCustomerRequest req) {
        CustomerResponse response = customerService.addCustomer(req);
        return ResponseEntity.ok(ApiResponse.success("Customer added successfully", response));
    }

    @GetMapping("/get-customer/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(@PathVariable String id)
            throws AuthException {
        CustomerResponse response = customerService.getCustomerById(id);
        return ResponseEntity.ok(ApiResponse.success("Customer fetched successfully", response));
    }

    @GetMapping("/get-all-customers")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllCustomers(
            Authentication authentication, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        User currentUser = (User) authentication.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<CustomerResponse> customerPage =
                customerService.getAllCustomers(currentUser.getId(), pageable);

        Pagination pagination = new Pagination(customerPage.getNumber(), customerPage.getSize(),
                customerPage.getTotalElements(), customerPage.getTotalPages());

        return ResponseEntity.ok(ApiResponse.success("Customers fetched successfully",
                customerPage.getContent(), pagination));
    }

    @PutMapping("update-customer/{id}")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(@PathVariable String id,
            @Valid @RequestBody AddCustomerRequest req) throws AuthException {
        CustomerResponse response = customerService.updateCustomer(id, req);
        return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", response));
    }

    @DeleteMapping("delete-customer/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable String id)
            throws AuthException {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully"));
    }
}
