package com.infynno.javastartup.startup.modules.services.dto;

import com.infynno.javastartup.startup.modules.services.model.Services;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServiceResponse {
    private String id;
    private String name;
    private String icon;
    private int price;
    private String nextService;

    public static ServiceResponse fromEntity(Services service) {
        return new ServiceResponse(service.getId(), service.getName(), service.getIcon(),
                service.getPrice(), service.getNextService());
    }
}
