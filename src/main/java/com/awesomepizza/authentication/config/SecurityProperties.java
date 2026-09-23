package com.awesomepizza.authentication.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "awesome-pizza.security")
public class SecurityProperties {
    private final Jwt jwt = new Jwt();
    private final Cors cors = new Cors();
    private final BootstrapAdmin bootstrapAdmin = new BootstrapAdmin();

    @Getter
    @Setter
    public static class Jwt {
        private String secretBase64 = "";
        private String issuer = "awesome-pizza";
        private Duration accessTokenTtl = Duration.ofHours(1);
    }

    @Getter
    @Setter
    public static class Cors {
        private List<String> allowedOrigins = new ArrayList<>(List.of("http://localhost:4200"));
    }

    @Getter
    @Setter
    public static class BootstrapAdmin {
        private String username = "";
        private String password = "";
    }
}



