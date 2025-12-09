package com.infynno.javastartup.startup.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) throws AuthException {
        if (auth == null) {
            throw new AuthException("Unauthorized");
        }
        return ResponseEntity.ok(auth.getPrincipal()); // will return User object from
                                                       // SecurityContext
    }
}
