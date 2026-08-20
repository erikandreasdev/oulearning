package com.example.oulearning;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class OulearningApplicationTests {

    @Test
    @DisplayName("given application context, when starting up, then context loads successfully")
    void givenAppContext_whenStartingUp_thenContextLoads(final ApplicationContext context) {
        // given

        // when

        // then
        assertThat(context).isNotNull();
    }
}
