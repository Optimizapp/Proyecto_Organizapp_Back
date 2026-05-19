package co.javeriana.dw.organizapp.security;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EndpointProtectionTest {

    private static final String SECRET =
            "clave-test-no-usar-en-produccion-debe-tener-minimo-64-caracteres-para-hs256-ok";

    private JwtProvider jwtProvider;
    private JwtAuthFilter jwtAuthFilter;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET, 86_400_000L);
        userRepository = mock(UserRepository.class);

        User user = buildUser();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);
        jwtAuthFilter = new JwtAuthFilter(jwtProvider, userDetailsService);

        SecurityContextHolder.clearContext();
    }

    @Test
    void requestWithoutTokenDoesNotSetAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/processes");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void requestWithValidTokenSetsAuthentication() throws Exception {
        String token = jwtProvider.generateToken(buildUser());

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/processes");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
                .isEqualTo("test@example.com");
    }

    @Test
    void requestWithTamperedTokenDoesNotSetAuthentication() throws Exception {
        String token = jwtProvider.generateToken(buildUser()) + "tampered";

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/processes");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void requestWithMalformedHeaderDoesNotSetAuthentication() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/processes");
        request.addHeader("Authorization", "Basic dXNlcjpwYXNz");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private User buildUser() {
        Company company = new Company();
        company.setId(1L);
        company.setName("TestCo");

        Role role = new Role();
        role.setId(1L);
        role.setNombre("ADMIN");

        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setName("Test");
        user.setActivo(true);
        user.setContrasenaHash("$2a$10$hash");
        user.setCompany(company);
        user.setRol(role);
        return user;
    }
}
