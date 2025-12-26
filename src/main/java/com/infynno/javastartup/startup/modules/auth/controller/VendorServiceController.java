package com.infynno.javastartup.startup.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infynno.javastartup.startup.modules.auth.dto.SelectVendorServicesRequest;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.service.VendorServiceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/vendor-service")
@RequiredArgsConstructor
public class VendorServiceController {
    private final VendorServiceService vendorServiceService;

    @PostMapping("select-services")
    public ResponseEntity<Void> selectVendorServices(
        @AuthenticationPrincipal User user,
        @Valid @RequestBody SelectVendorServicesRequest req
    ) {
        vendorServiceService.selectServices(user, req);
        return ResponseEntity.ok().build();
    }
}
