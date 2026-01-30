package com.infynno.javastartup.startup.modules.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.infynno.javastartup.startup.common.exceptions.AuthException;
import com.infynno.javastartup.startup.common.response.ApiResponse;
import com.infynno.javastartup.startup.modules.auth.dto.UpdateBusinessDetailsRequest;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> me(Authentication auth) throws AuthException {
        if (auth == null) {
            throw new AuthException("Unauthorized");
        }
        return ResponseEntity
                .ok(ApiResponse.success("User fetched successfully", (User) auth.getPrincipal()));
    }

    @PutMapping("/update-business-details")
    public ResponseEntity<ApiResponse<User>> updateBusinessDetails(
            @Valid @RequestBody UpdateBusinessDetailsRequest req, Authentication authentication)
            throws AuthException {
        User currentUser = (User) authentication.getPrincipal();
        // Assuming userService is injected and has the method updateBusinessDetails
        userService.updateBusinessDetails(currentUser, req);
        return ResponseEntity
                .ok(ApiResponse.success("Business details updated successfully", currentUser));
    }
}
