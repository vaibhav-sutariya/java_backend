package com.infynno.javastartup.startup.modules.services.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.common.response.ApiResponse;
import com.infynno.javastartup.startup.modules.services.dto.ServiceResponse;
import com.infynno.javastartup.startup.modules.services.model.Services;
import com.infynno.javastartup.startup.modules.services.repository.ServiceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicesService {
    @Autowired
    final ServiceRepository serviceRepository;

    @Transactional
    public ApiResponse<List<ServiceResponse>> getAllServices() throws AuthException {
        List<Services> services = serviceRepository.findAll();
        List<ServiceResponse> serviceResponses = services.stream()
                .map(ServiceResponse::fromEntity)
                .collect(Collectors.toList());

        return ApiResponse.success("Services fetched successfully", serviceResponses);
    }
    
}
