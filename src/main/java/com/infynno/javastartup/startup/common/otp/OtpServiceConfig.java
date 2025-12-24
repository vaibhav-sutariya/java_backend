package com.infynno.javastartup.startup.common.otp;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class OtpServiceConfig {
    
    private final List<OtpProvider> otpProviders;

    @Bean
    public Map<String, OtpProvider> otpProviderMap() {
        return otpProviders.stream()
                .collect(Collectors.toMap(
                        OtpProvider::getName,
                        Function.identity()
                ));
    }
}