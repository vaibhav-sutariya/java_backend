package com.infynno.javastartup.startup.modules.auth.mapper;

import org.springframework.stereotype.Component;
import com.infynno.javastartup.startup.modules.auth.dto.RegisterResponse;
import com.infynno.javastartup.startup.modules.auth.model.User;

@Component
public class AuthMapper {

    public RegisterResponse toRegisterResponse(User user, String message) {
        if (user == null) {
            return null;
        }
        return new RegisterResponse(user.getId(), user.getEmail(), user.getName(), message);
    }
}
