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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests de autorización por rol y aislamiento de tenant.
 *
 * Verifica que JwtAuthFilter establece correctamente las authorities
 * según el rol del token, propaga el companyId como atributo de request,
 * y rechaza tokens inválidos (firma incorrecta, expirados).
 */
class RoleBasedAccessTest {

    private static final String SECRET =
            "clave-test-no-usar-en-produccion-debe-tener-minimo-64-caracteres-para-hs256-ok";

    private JwtProvider jwtProvider;
    private JwtAuthFilter jwtAuthFilter;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        jwtProvider = new JwtProvider(SECRET, 86_400_000L);
        userRepository = mock(UserRepository.class);

        // Registrar ambos usuarios por email para los tests que los necesitan
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(buildUser("admin@example.com", "ADMIN", 1L, 10L)));
        when(userRepository.findByEmail("user@example.com"))
                .thenReturn(Optional.of(buildUser("user@example.com", "USER", 2L, 10L)));
        when(userRepository.findByEmail("tenant1@example.com"))
                .thenReturn(Optional.of(buildUser("tenant1@example.com", "USER", 3L, 1L)));
        when(userRepository.findByEmail("tenant2@example.com"))
                .thenReturn(Optional.of(buildUser("tenant2@example.com", "USER", 4L, 2L)));

        UserDetailsServiceImpl userDetailsService = new UserDetailsServiceImpl(userRepository);
        jwtAuthFilter = new JwtAuthFilter(jwtProvider, userDetailsService);

        SecurityContextHolder.clearContext();
    }

    // -------------------------------------------------------------------------
    // Test 2: token con rol ADMIN establece la authority ROLE_ADMIN
    // -------------------------------------------------------------------------

    /**
     * Un token generado para un usuario ADMIN hace que el filtro establezca
     * la authority "ROLE_ADMIN" en el SecurityContext.
     */
    @Test
    void tokenWithAdminRoleSetsAdminAuthority() throws Exception {
        User adminUser = buildUser("admin@example.com", "ADMIN", 1L, 10L);
        String token = jwtProvider.generateToken(adminUser);

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/processes");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");
    }

    // -------------------------------------------------------------------------
    // Test 3: token con rol USER establece la authority ROLE_USER (no ROLE_ADMIN)
    // -------------------------------------------------------------------------

    /**
     * Un token generado para un usuario USER hace que el filtro establezca
     * la authority "ROLE_USER" — nunca "ROLE_ADMIN".
     */
    @Test
    void tokenWithUserRoleSetsUserAuthorityOnly() throws Exception {
        User regularUser = buildUser("user@example.com", "USER", 2L, 10L);
        String token = jwtProvider.generateToken(regularUser);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/processes");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertThat(auth).isNotNull();

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        assertThat(authorities)
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_USER")
                .doesNotContain("ROLE_ADMIN");
    }

    // -------------------------------------------------------------------------
    // Test 4: el filtro propaga el companyId del token como atributo de request
    // -------------------------------------------------------------------------

    /**
     * El filtro lee el claim "companyId" del JWT y lo establece como atributo
     * "companyId" en el HttpServletRequest, que es lo que consume SecurityUtils.
     */
    @Test
    void filterSetsCompanyIdAsRequestAttribute() throws Exception {
        long expectedCompanyId = 42L;
        User user = buildUser("admin@example.com", "ADMIN", 1L, expectedCompanyId);
        // Registrar el email del usuario en el mock para este companyId específico
        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(user));
        String token = jwtProvider.generateToken(user);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/processes");
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertThat(request.getAttribute("companyId")).isEqualTo(expectedCompanyId);
    }

    // -------------------------------------------------------------------------
    // Test 5: un token firmado con un secret diferente es rechazado
    // -------------------------------------------------------------------------

    /**
     * Un token generado por un JwtProvider que usa un secret diferente tiene
     * una firma inválida. El filtro debe rechazarlo y no establecer autenticación.
     */
    @Test
    void tokenSignedWithDifferentSecretIsRejected() throws Exception {
        JwtProvider otherProvider = new JwtProvider(
                "otro-secret-completamente-diferente-y-largo-de-al-menos-64-chars-aqui",
                86_400_000L);
        User user = buildUser("admin@example.com", "ADMIN", 1L, 10L);
        String foreignToken = otherProvider.generateToken(user);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/processes");
        request.addHeader("Authorization", "Bearer " + foreignToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    /**
     * Un token generado con expirationMs negativo ya nació vencido.
     * El filtro debe rechazarlo y no establecer autenticación.
     */
    @Test
    void expiredTokenIsRejected() throws Exception {
        JwtProvider expiredProvider = new JwtProvider(SECRET, -1000L);
        User user = buildUser("admin@example.com", "ADMIN", 1L, 10L);
        String expiredToken = expiredProvider.generateToken(user);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/processes");
        request.addHeader("Authorization", "Bearer " + expiredToken);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtAuthFilter.doFilter(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    // -------------------------------------------------------------------------
    // Test 6: aislamiento de tenant — cada token propaga su propio companyId
    // -------------------------------------------------------------------------

    /**
     * Dos tokens con companyId distintos ponen valores distintos en el atributo
     * "companyId" de sus respectivos requests. El valor de un request no contamina
     * al otro (aislamiento de tenant por request).
     */
    @Test
    void tenantIsolationViaRequestAttribute() throws Exception {
        // Token de tenant 1
        User tenant1User = buildUser("tenant1@example.com", "USER", 3L, 1L);
        String token1 = jwtProvider.generateToken(tenant1User);

        MockHttpServletRequest request1 = new MockHttpServletRequest("GET", "/api/processes");
        request1.addHeader("Authorization", "Bearer " + token1);
        MockHttpServletResponse response1 = new MockHttpServletResponse();
        MockFilterChain chain1 = new MockFilterChain();

        jwtAuthFilter.doFilter(request1, response1, chain1);
        SecurityContextHolder.clearContext();

        // Token de tenant 2
        User tenant2User = buildUser("tenant2@example.com", "USER", 4L, 2L);
        String token2 = jwtProvider.generateToken(tenant2User);

        MockHttpServletRequest request2 = new MockHttpServletRequest("GET", "/api/processes");
        request2.addHeader("Authorization", "Bearer " + token2);
        MockHttpServletResponse response2 = new MockHttpServletResponse();
        MockFilterChain chain2 = new MockFilterChain();

        jwtAuthFilter.doFilter(request2, response2, chain2);

        // Cada request tiene su propio companyId
        assertThat(request1.getAttribute("companyId")).isEqualTo(1L);
        assertThat(request2.getAttribute("companyId")).isEqualTo(2L);

        // Los valores son distintos entre sí
        assertThat(request1.getAttribute("companyId"))
                .isNotEqualTo(request2.getAttribute("companyId"));
    }

    // -------------------------------------------------------------------------
    // Helper
    // -------------------------------------------------------------------------

    /**
     * Construye un User con email, rol, userId y companyId configurables.
     */
    private User buildUser(String email, String rolNombre, long userId, long companyId) {
        Company company = new Company();
        company.setId(companyId);
        company.setName("Company-" + companyId);

        Role role = new Role();
        role.setId(1L);
        role.setNombre(rolNombre);

        User user = new User();
        user.setId(userId);
        user.setEmail(email);
        user.setName("User " + email);
        user.setActivo(true);
        user.setContrasenaHash("$2a$10$hash");
        user.setCompany(company);
        user.setRol(role);
        return user;
    }
}
