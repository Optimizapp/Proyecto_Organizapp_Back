package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.*;
import co.javeriana.dw.organizapp.entity.*;
import co.javeriana.dw.organizapp.repository.*;
import co.javeriana.dw.organizapp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private ModelMapper modelMapper;

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
        user.setCompany(company);
        user.setRol(role);

        return user;
    }

    private UserResponseDto buildResponseDto() {
        UserResponseDto dto = new UserResponseDto();
        dto.setCompanyId(1L);
        dto.setRoleId(1L);
        dto.setRoleNombre("ADMIN");
        return dto;
    }

    @Test
    void findAll_shouldReturnUsers() {
        User user = buildUser();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class)))
                .thenReturn(buildResponseDto());

        List<UserResponseDto> result = userService.findAll();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void findById_shouldReturnUser() {
        User user = buildUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class)))
                .thenReturn(buildResponseDto());

        UserResponseDto result = userService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void create_shouldSaveUser() {
        UserRequestDto request = new UserRequestDto();
        request.setCompanyId(1L);
        request.setRoleId(1L);

        Company company = new Company();
        company.setId(1L);

        Role role = new Role();
        role.setId(1L);

        User user = buildUser();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(modelMapper.map(request, User.class)).thenReturn(user);
        when(userRepository.save(any())).thenReturn(user);
        when(modelMapper.map(any(User.class), eq(UserResponseDto.class)))
                .thenReturn(buildResponseDto());

        UserResponseDto result = userService.create(request);

        assertNotNull(result);
        verify(userRepository).save(any());
    }

    @Test
    void delete_shouldCallRepository() {
        User user = buildUser();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.delete(1L);

        verify(userRepository).delete(user);
    }
}