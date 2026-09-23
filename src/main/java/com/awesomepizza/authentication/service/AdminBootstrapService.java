package com.awesomepizza.authentication.service;

import com.awesomepizza.authentication.entity.AppUserDB;
import com.awesomepizza.authentication.enumeration.UserRole;
import com.awesomepizza.authentication.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBootstrapService {
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public boolean createPizzaMakerIfMissing(String username, String rawPassword) {
        String normalizedUsername = username.trim();
        if (appUserRepository.existsByUsernameIgnoreCase(normalizedUsername)) {
            return false;
        }

        AppUserDB user = AppUserDB.create();
        user.setUsername(normalizedUsername);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(UserRole.PIZZA_MAKER);
        user.setEnabled(true);
        appUserRepository.save(user);
        return true;
    }
}



