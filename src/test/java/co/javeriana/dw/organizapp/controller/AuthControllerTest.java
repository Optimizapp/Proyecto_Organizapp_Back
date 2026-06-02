package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.GlobalExceptionHandler;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.security.JwtProvider;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private static final String SECRET =
            "clave-test-no-usar-en-produccion-debe-tener-minimo-64-caracteres-para-hs256-ok";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        JwtProvider jwtProvider = new JwtProvider(SECRET, 86_400_000L);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(userRepository, passwordEncoder, jwtProvider,
                        new SimpleMeterRegistry()))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void loginWithValidCredentialsReturns200AndToken() throws Exception {
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(buildActiveUser("admin@test.com", "secret123")));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@test.com","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.userEmail").value("admin@test.com"));
    }

    @Test
    void loginWithAdminEmailAliasReturns200AndToken() throws Exception {
        when(userRepository.findByEmail("adminEmail@gmail.com"))
                .thenReturn(Optional.of(buildActiveUser("adminEmail@gmail.com", "adminPassword")));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"adminEmail":"adminEmail@gmail.com","adminPassword":"adminPassword"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.userEmail").value("adminEmail@gmail.com"));
    }

    @Test
    void loginWithUnknownEmailReturns401() throws Exception {
        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"unknown@test.com","password":"any"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        when(userRepository.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(buildActiveUser("admin@test.com", "correct")));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"admin@test.com","password":"wrong"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void loginWithInactiveUserReturns403() throws Exception {
        User inactive = buildActiveUser("inactive@test.com", "pass");
        inactive.setActivo(false);
        when(userRepository.findByEmail("inactive@test.com")).thenReturn(Optional.of(inactive));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"inactive@test.com","password":"pass"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Usuario inactivo"));
    }

    private User buildActiveUser(String email, String rawPassword) {
        Company company = new Company();
        company.setId(1L);
        company.setName("TestCo");

        Role role = new Role();
        role.setId(1L);
        role.setNombre("ADMIN");

        User user = new User();
        user.setId(1L);
        user.setEmail(email);
        user.setName("Test User");
        user.setContrasenaHash(passwordEncoder.encode(rawPassword));
        user.setActivo(true);
        user.setCompany(company);
        user.setRol(role);
        return user;
    }
}
