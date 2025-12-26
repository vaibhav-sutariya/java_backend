package com.infynno.javastartup.startup.modules.auth.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.infynno.javastartup.startup.common.response.ApiResponse;
import com.infynno.javastartup.startup.modules.auth.dto.SelectVendorServicesRequest;
import com.infynno.javastartup.startup.modules.auth.dto.VendorServiceResponse;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.model.VendorService;
import com.infynno.javastartup.startup.modules.auth.repository.VendorServiceRepository;
import com.infynno.javastartup.startup.modules.services.model.Services;
import com.infynno.javastartup.startup.modules.services.repository.ServiceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendorServiceService {
    private final ServiceRepository serviceRepository;
    private final VendorServiceRepository vendorServiceRepository;

    public ApiResponse<List<VendorServiceResponse>> selectServices(User user, SelectVendorServicesRequest req){
        List<VendorService> vendorServices = new ArrayList<>();

        for(String serviceId : req.getServiceIds()){
            Services service = serviceRepository.findById(serviceId)
    .orElseThrow(() -> new RuntimeException("Service not found"));

            if(vendorServiceRepository.existsByCreatedByAndServiceId(user, service)){
                continue; // Skip if already selected
            }

          Services services = serviceRepository.findById(serviceId).orElseThrow(
                () -> new IllegalArgumentException("Service with ID " + serviceId + " does not exist.")
            );

            VendorService vendorService = VendorService.builder()
                    .serviceId(services)
                    .name(services.getName())
                    .icon(services.getIcon())
                    .price(services.getPrice())
                    .nextService(services.getNextService())
                    .isCustom(false)
                    .createdBy(user)
                    .createdAt(Instant.now())
                    .build();
            vendorServices.add(vendorService);
            
        }
        vendorServiceRepository.saveAll(vendorServices);
        return ApiResponse.success("Vendor services selected successfully");
    }

    public ApiResponse<List<VendorServiceResponse>> getVendorServicesByUser(User user){
        List<VendorService> vendorServices = vendorServiceRepository.findByCreatedBy(user);
        List<VendorServiceResponse> responseList = new ArrayList<>();
        for(VendorService vs : vendorServices){
            responseList.add(VendorServiceResponse.fromEntity(vs));
        }
        return ApiResponse.success("Vendor services fetched successfully", responseList);
    }
}
