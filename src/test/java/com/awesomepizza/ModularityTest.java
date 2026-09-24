package com.awesomepizza;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

import static org.assertj.core.api.Assertions.assertThat;

class ModularityTest {

    @Test
    void verifiesModuleBoundaries() {
        ApplicationModules modules = ApplicationModules.of(AwesomePizzaApplication.class);
        modules.verify();
        assertThat(modules.stream().map(module -> module.getIdentifier().toString()))
                .containsExactlyInAnyOrder("ordering", "authentication", "shared");
    }
}
