package com.awesomepizza.authentication.service;

import com.awesomepizza.authentication.entity.AppUserDB;
import com.awesomepizza.authentication.enumeration.UserRole;
import com.awesomepizza.authentication.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBootstrapServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Test
    void createsPizzaMakerWithBcryptPasswordWhenUsernameDoesNotExist() {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        AdminBootstrapService service = new AdminBootstrapService(appUserRepository, passwordEncoder);
        when(appUserRepository.existsByUsernameIgnoreCase("pizza-maker")).thenReturn(false);

        boolean created = service.createPizzaMakerIfMissing(" pizza-maker ", "temporary-password");

        ArgumentCaptor<AppUserDB> userCaptor = ArgumentCaptor.forClass(AppUserDB.class);
        verify(appUserRepository).save(userCaptor.capture());
        AppUserDB savedUser = userCaptor.getValue();

        assertThat(created).isTrue();
        assertThat(savedUser.getUsername()).isEqualTo("pizza-maker");
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("temporary-password");
        assertThat(passwordEncoder.matches("temporary-password", savedUser.getPasswordHash())).isTrue();
        assertThat(savedUser.getRole()).isEqualTo(UserRole.PIZZA_MAKER);
        assertThat(savedUser.isEnabled()).isTrue();
    }

    @Test
    void doesNotReplaceAnExistingPizzaMaker() {
        AdminBootstrapService service =
                new AdminBootstrapService(appUserRepository, new BCryptPasswordEncoder());
        when(appUserRepository.existsByUsernameIgnoreCase("pizza-maker")).thenReturn(true);

        boolean created = service.createPizzaMakerIfMissing("pizza-maker", "temporary-password");

        assertThat(created).isFalse();
        verify(appUserRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}



