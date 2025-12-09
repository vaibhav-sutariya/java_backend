package com.infynno.javastartup.startup.modules.auth.dto;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refreshToken;

    public String getRefreshToken() {
        return refreshToken;
    }
}
