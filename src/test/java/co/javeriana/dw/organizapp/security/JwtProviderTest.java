package co.javeriana.dw.organizapp.security;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private static final String SECRET =
            "clave-test-no-usar-en-produccion-debe-tener-minimo-64-caracteres-para-hs256-ok";
    private static final long EXPIRATION_MS = 86_400_000L;
    private static final long EXPIRED_MS = -1000L;

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET, EXPIRATION_MS);
    }

    @Test
    void generateTokenReturnsNonNullString() {
        String token = jwtProvider.generateToken(buildUser());
        assertThat(token).isNotNull().isNotBlank();
    }

    @Test
    void validateTokenReturnsTrueForValidToken() {
        String token = jwtProvider.generateToken(buildUser());
        assertThat(jwtProvider.validateToken(token)).isTrue();
    }

    @Test
    void validateTokenReturnsFalseForExpiredToken() {
        JwtProvider expiredProvider = new JwtProvider(SECRET, EXPIRED_MS);
        String token = expiredProvider.generateToken(buildUser());
        assertThat(jwtProvider.validateToken(token)).isFalse();
    }

    @Test
    void validateTokenReturnsFalseForTamperedToken() {
        String token = jwtProvider.generateToken(buildUser()) + "tampered";
        assertThat(jwtProvider.validateToken(token)).isFalse();
    }

    @Test
    void getEmailFromTokenReturnsCorrectSubject() {
        String token = jwtProvider.generateToken(buildUser());
        assertThat(jwtProvider.getEmailFromToken(token)).isEqualTo("test@example.com");
    }

    @Test
    void getUserIdFromTokenReturnsCorrectId() {
        String token = jwtProvider.generateToken(buildUser());
        assertThat(jwtProvider.getUserIdFromToken(token)).isEqualTo(1L);
    }

    private User buildUser() {
        Company company = new Company();
        company.setId(10L);
        company.setName("TestCo");

        Role role = new Role();
        role.setId(2L);
        role.setNombre("ADMIN");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setCompany(company);
        user.setRol(role);
        return user;
    }
}
