package co.javeriana.dw.thymeleaf;

import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ServletInitializerTest {

    @Test
    void shouldConfigureApplicationSources() {
        ServletInitializer initializer = new ServletInitializer();

        SpringApplicationBuilder builder = initializer.configure(new SpringApplicationBuilder());

        assertNotNull(builder);
    }
}
