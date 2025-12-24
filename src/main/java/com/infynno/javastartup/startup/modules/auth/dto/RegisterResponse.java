package com.infynno.javastartup.startup.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String userId;
    private String email;
    private String name;
    private String message;
}