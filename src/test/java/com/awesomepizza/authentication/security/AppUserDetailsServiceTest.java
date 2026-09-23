package com.awesomepizza.authentication.security;

import com.awesomepizza.authentication.entity.AppUserDB;
import com.awesomepizza.authentication.enumeration.UserRole;
import com.awesomepizza.authentication.mapper.AppUserMapper;
import com.awesomepizza.authentication.model.AppUserModel;
import com.awesomepizza.authentication.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private AppUserMapper appUserMapper;

    private AppUserDetailsService service;

    @BeforeEach
    void setUp() {
        service = new AppUserDetailsService(appUserRepository, appUserMapper);
    }

    @Test
    void loadsThePizzaMakerWithRoleAndPasswordHash() {
        AppUserDB entity = AppUserDB.create();
        AppUserModel model = AppUserModel.builder()
                .username("pizzaiolo")
                .passwordHash("{bcrypt}hash")
                .role(UserRole.PIZZA_MAKER)
                .enabled(true)
                .build();
        when(appUserRepository.findByUsernameIgnoreCase("PIZZAIOLO"))
                .thenReturn(Optional.of(entity));
        when(appUserMapper.toModel(entity)).thenReturn(model);

        var userDetails = service.loadUserByUsername("PIZZAIOLO");

        assertThat(userDetails.getUsername()).isEqualTo("pizzaiolo");
        assertThat(userDetails.getPassword()).isEqualTo("{bcrypt}hash");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly("ROLE_PIZZA_MAKER");
    }

    @Test
    void rejectsAnUnknownUsername() {
        when(appUserRepository.findByUsernameIgnoreCase("unknown"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
