package co.javeriana.dw.thymeleaf;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class ThymeleafApplicationTest {

    @Test
    void shouldRunMainMethod() {
        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            ThymeleafApplication.main(new String[]{"arg"});

            mockedSpringApplication.verify(() -> SpringApplication.run(ThymeleafApplication.class, new String[]{"arg"}));
        }
    }
}
