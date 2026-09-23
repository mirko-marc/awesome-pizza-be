package com.awesomepizza.authentication.config;

import com.awesomepizza.authentication.service.AdminBootstrapService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminBootstrapConfig implements ApplicationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBootstrapConfig.class);

    private final SecurityProperties securityProperties;
    private final AdminBootstrapService adminBootstrapService;

    @Override
    public void run(ApplicationArguments arguments) {
        String username = securityProperties.getBootstrapAdmin().getUsername();
        String password = securityProperties.getBootstrapAdmin().getPassword();
        boolean usernameConfigured = username != null && !username.isBlank();
        boolean passwordConfigured = password != null && !password.isBlank();

        if (!usernameConfigured && !passwordConfigured) {
            return;
        }
        if (!usernameConfigured || !passwordConfigured) {
            throw new IllegalStateException(
                    "ADMIN_USERNAME and ADMIN_PASSWORD must either both be configured or both be omitted");
        }

        boolean created = adminBootstrapService.createPizzaMakerIfMissing(username, password);
        if (created) {
            LOGGER.info("Bootstrap pizza maker account created for username '{}'", username);
        } else {
            LOGGER.info("Bootstrap pizza maker account '{}' already exists", username);
        }
    }
}



