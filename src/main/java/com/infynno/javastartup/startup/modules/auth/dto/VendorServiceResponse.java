package com.infynno.javastartup.startup.modules.auth.dto;

import com.infynno.javastartup.startup.modules.auth.model.VendorService;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VendorServiceResponse {
    private String id;
    private String name;
    private String icon;
    private int  price;
    private String nextService;


    public static VendorServiceResponse fromEntity(VendorService vendorService) {
        return new VendorServiceResponse(
                vendorService.getId(),
                vendorService.getName(),
                vendorService.getIcon(),
                vendorService.getPrice(),
                vendorService.getNextService()
        );
    }
}
