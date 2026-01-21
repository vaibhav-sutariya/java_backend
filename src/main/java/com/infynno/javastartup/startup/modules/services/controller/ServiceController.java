package com.infynno.javastartup.startup.modules.services.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.common.response.ApiResponse;
import com.infynno.javastartup.startup.modules.services.dto.ServiceResponse;
import com.infynno.javastartup.startup.modules.services.service.ServicesService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServicesService servicesService;

    @GetMapping("/get-all-services")
    public ResponseEntity<ApiResponse<List<ServiceResponse>>> getAllServices()
            throws AuthException {
        return ResponseEntity.ok(ApiResponse.success("Services fetched successfully",
                servicesService.getAllServices()));
    }
}
