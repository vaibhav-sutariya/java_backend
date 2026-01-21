package com.infynno.javastartup.startup.modules.services.mapper;

import org.springframework.stereotype.Component;
import com.infynno.javastartup.startup.modules.services.dto.ServiceResponse;
import com.infynno.javastartup.startup.modules.services.model.Services;

@Component
public class ServiceMapper {

    public ServiceResponse toResponse(Services service) {
        if (service == null) {
            return null;
        }

        return new ServiceResponse(service.getId(), service.getName(), service.getIcon(),
                service.getPrice(), service.getNextService());
    }
}
