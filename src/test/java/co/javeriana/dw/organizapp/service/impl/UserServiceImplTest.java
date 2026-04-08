package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.UserRequestDto;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository; // 👈 FALTABA

    @Mock
    private RoleRepository roleRepository; // 👈 FALTABA

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    // 🔧 helper
    private User buildUser() {
        Company company = new Company();
        company.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setNombre("ADMIN");

        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setCompany(company);
        user.setRol(role);

        return user;
    }

    private UserResponseDto buildDto() {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(1L);
        dto.setName("Test User");
        dto.setCompanyId(1L);
        dto.setRoleId(1L);
        dto.setRoleNombre("ADMIN");
        return dto;
    }

    @Test
    void testFindAll() {
        User user = buildUser();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class)))
                .thenReturn(buildDto());

        List<UserResponseDto> result = userService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test User", result.get(0).getName());

        verify(userRepository).findAll();
    }

    @Test
    void testFindByIdSuccess() {
        User user = buildUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class)))
                .thenReturn(buildDto());

        UserResponseDto result = userService.findById(1L);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.findById(1L));
    }

    @Test
    void testCreate() {
        UserRequestDto requestDto = new UserRequestDto();
        requestDto.setName("Test User");
        requestDto.setCompanyId(1L); // 👈 CLAVE
        requestDto.setRoleId(1L);    // 👈 CLAVE

        Company company = new Company();
        company.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = buildUser();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(modelMapper.map(any(UserRequestDto.class), eq(User.class)))
                .thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class)))
                .thenReturn(buildDto());

        UserResponseDto result = userService.create(requestDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDelete() {
        User user = buildUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }
}