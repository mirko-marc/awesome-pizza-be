package com.awesomepizza.authentication.config;

import com.awesomepizza.authentication.service.AdminBootstrapService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AdminBootstrapConfigTest {

    private final AdminBootstrapService adminBootstrapService = mock(AdminBootstrapService.class);
    private final SecurityProperties properties = new SecurityProperties();
    private final AdminBootstrapConfig config =
            new AdminBootstrapConfig(properties, adminBootstrapService);

    @Test
    void createsTheConfiguredAdministratorAtStartup() {
        properties.getBootstrapAdmin().setUsername("pizzaiolo");
        properties.getBootstrapAdmin().setPassword("PasswordSicura123!");
        when(adminBootstrapService.createPizzaMakerIfMissing(
                "pizzaiolo", "PasswordSicura123!")).thenReturn(true);

        config.run(null);

        verify(adminBootstrapService).createPizzaMakerIfMissing(
                "pizzaiolo", "PasswordSicura123!");
    }

    @Test
    void skipsBootstrapWhenCredentialsAreNotConfigured() {
        config.run(null);

        verify(adminBootstrapService, never())
                .createPizzaMakerIfMissing(org.mockito.ArgumentMatchers.anyString(),
                        org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void rejectsAPartiallyConfiguredAdministrator() {
        properties.getBootstrapAdmin().setUsername("pizzaiolo");

        assertThatThrownBy(() -> config.run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ADMIN_USERNAME and ADMIN_PASSWORD");
    }
}
