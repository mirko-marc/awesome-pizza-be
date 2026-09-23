package com.awesomepizza.shared.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeConfig {

    @Bean
    Clock applicationClock(@Value("${awesome-pizza.time-zone:Europe/Rome}") String timeZone) {
        return Clock.system(ZoneId.of(timeZone));
    }
}
