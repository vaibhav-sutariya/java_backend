package com.infynno.javastartup.startup.modules.services.seed;

import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.infynno.javastartup.startup.modules.services.model.Services;
import com.infynno.javastartup.startup.modules.services.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceDataSeeder implements CommandLineRunner {
    private final ServiceRepository serviceRepository;

    @Override
    public void run(String... args) throws Exception {
        if(serviceRepository.count() > 0) {
            return;
        }
        
        List<Services> services = List.of(
            Services.builder()
                .icon("water-purifier-icon.svg")
                .name("R.O Purifier")
                .price(236)
                .nextService("3 months")
                .build(),

            Services.builder()
                .icon("ac.svg")
                .name("Air Conditioner")
                .price(236)
                .nextService("3 months")
                .build(),

            Services.builder()
                .icon("washing-machine-icon.svg")
                .name("Washing Machine")
                .price(236)
                .nextService("3 months")
                .build()
        );

        serviceRepository.saveAll(services);
    }
}
