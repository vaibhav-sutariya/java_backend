package com.infynno.javastartup.startup.modules.auth.service;

import java.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.infynno.javastartup.startup.modules.auth.dto.UpdateBusinessDetailsRequest;
import com.infynno.javastartup.startup.modules.auth.model.User;
import com.infynno.javastartup.startup.modules.auth.model.VendorService;
import com.infynno.javastartup.startup.modules.auth.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final RefreshTokenService refreshService;

    @Transactional
    public void changePassword(User user, String newPassword) {
        user.setPassword(encoder.encode(newPassword));
        user.setTokenInvalidBefore(Instant.now()); // Invalidate all old tokens
        refreshService.revokeAllForUser(user, "password_change");
        repo.save(user);
    }

    @Transactional
    public void updateBusinessDetails(User user, UpdateBusinessDetailsRequest req) {

        if (req.getBusinessName() != null)
            user.setBusinessName(req.getBusinessName());

        if (req.getBusinessAddress() != null)
            user.setBusinessAddress(req.getBusinessAddress());

        if (req.getPhoneNumber() != null)
            user.setPhoneNumber(req.getPhoneNumber());

        if (req.getCity() != null)
            user.setCity(req.getCity());

        if (req.getState() != null)
            user.setState(req.getState());

        if (req.getZipCode() != null)
            user.setZipCode(req.getZipCode());

        if (req.getGstNumber() != null)
            user.setGstNumber(req.getGstNumber());

        if (req.getNotes() != null)
            user.setNotes(req.getNotes());

        if (req.getGstPercentage() != null)
            user.setGstPercentage(req.getGstPercentage());

        repo.save(user);
    }

    @Transactional
    public void selectServices(User user, String[] serviceIds) {
        VendorService[] services = new VendorService[serviceIds.length];
        for (int i = 0; i < serviceIds.length; i++) {
            services[i].setId(serviceIds[i]);
        }
    }
}
