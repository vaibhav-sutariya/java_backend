package com.infynno.javastartup.startup.modules.auth.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.infynno.javastartup.startup.modules.auth.model.RefreshToken;
import com.infynno.javastartup.startup.modules.auth.model.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAllByUser(User user);

    List<RefreshToken> findByUserAndFamily(User user, String family);

    void deleteAllByUser(User user);

}
