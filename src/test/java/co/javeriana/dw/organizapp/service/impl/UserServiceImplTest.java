package co.javeriana.dw.organizapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.javeriana.dw.organizapp.dto.CreateUserRequest;
import co.javeriana.dw.organizapp.dto.UpdateUserRequest;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceImplTest {

    private UserRepository userRepository;
    private CompanyRepository companyRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        companyRepository = mock(CompanyRepository.class);
        roleRepository = mock(RoleRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(
                userRepository,
                companyRepository,
                roleRepository,
                new ModelMapper(),
                passwordEncoder);
    }

    @Test
    void createUserAllowsRoleFromSameCompany() {
        Company company = company(1L);
        Role role = role(10L, company);
        CreateUserRequest request = createRequest(1L, 10L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(roleRepository.findById(10L)).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password123")).thenReturn("hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(20L);
            return user;
        });

        UserResponseDto response = userService.create(request);

        assertThat(response.getId()).isEqualTo(20L);
        assertThat(response.getCompanyId()).isEqualTo(1L);
        assertThat(response.getRoleId()).isEqualTo(10L);
    }

    @Test
    void createUserRejectsRoleFromAnotherCompany() {
        CreateUserRequest request = createRequest(1L, 10L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
        when(roleRepository.findById(10L)).thenReturn(Optional.of(role(10L, company(2L))));

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El rol no pertenece a la empresa indicada");
    }

    @Test
    void updateUserRejectsRoleFromAnotherCompany() {
        User existingUser = new User();
        existingUser.setId(20L);
        existingUser.setEmail("diego@example.com");
        UpdateUserRequest request = updateRequest(1L, 10L);
        when(userRepository.findById(20L)).thenReturn(Optional.of(existingUser));
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company(1L)));
        when(roleRepository.findById(10L)).thenReturn(Optional.of(role(10L, company(2L))));

        assertThatThrownBy(() -> userService.update(20L, request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El rol no pertenece a la empresa indicada");
    }

    private static CreateUserRequest createRequest(Long companyId, Long roleId) {
        CreateUserRequest request = new CreateUserRequest();
        request.setName("Diego");
        request.setEmail("diego@example.com");
        request.setPassword("password123");
        request.setCompanyId(companyId);
        request.setRoleId(roleId);
        return request;
    }

    private static UpdateUserRequest updateRequest(Long companyId, Long roleId) {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Diego");
        request.setEmail("diego@example.com");
        request.setCompanyId(companyId);
        request.setRoleId(roleId);
        request.setActive(true);
        return request;
    }

    private static Company company(Long id) {
        Company company = new Company();
        company.setId(id);
        return company;
    }

    private static Role role(Long id, Company company) {
        Role role = new Role();
        role.setId(id);
        role.setNombre("EDITOR");
        role.setCompany(company);
        return role;
    }
}
