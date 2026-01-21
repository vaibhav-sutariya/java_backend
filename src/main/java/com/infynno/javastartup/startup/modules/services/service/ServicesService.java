package com.infynno.javastartup.startup.modules.services.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.infynno.javastartup.startup.modules.services.dto.ServiceResponse;
import com.infynno.javastartup.startup.modules.services.mapper.ServiceMapper;
import com.infynno.javastartup.startup.modules.services.model.Services;
import com.infynno.javastartup.startup.modules.services.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServicesService {

    private final ServiceRepository serviceRepository;
    private final ServiceMapper serviceMapper;

    public List<ServiceResponse> getAllServices() {
        List<Services> services = serviceRepository.findAll();
        return services.stream().map(serviceMapper::toResponse).collect(Collectors.toList());
    }

}
