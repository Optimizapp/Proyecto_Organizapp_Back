package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
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
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ProcessRepository processRepository; // 👈 FALTABA

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    // 🔧 helper clave
    private Role buildRole() {
        Process process = new Process();
        process.setId(1L);

        Role role = new Role();
        role.setId(1L);
        role.setNombre("Admin");
        role.setProceso(process); // 👈 ESTO FALTABA

        return role;
    }

    private RoleResponseDto buildDto() {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(1L);
        dto.setNombre("Admin");
        dto.setProcessId(1L);
        return dto;
    }

    @Test
    void testFindAll() {
        Role role = buildRole();

        when(roleRepository.findAll()).thenReturn(List.of(role));
        when(modelMapper.map(any(Role.class), eq(RoleResponseDto.class)))
                .thenReturn(buildDto());

        List<RoleResponseDto> result = roleService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Admin", result.get(0).getNombre());

        verify(roleRepository).findAll();
    }

    @Test
    void testFindByIdSuccess() {
        Role role = buildRole();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(modelMapper.map(any(Role.class), eq(RoleResponseDto.class)))
                .thenReturn(buildDto());

        RoleResponseDto result = roleService.findById(1L);

        assertNotNull(result);
        assertEquals("Admin", result.getNombre());
    }

    @Test
    void testFindByIdNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> roleService.findById(1L));
    }

    @Test
    void testCreate() {
        RoleRequestDto requestDto = new RoleRequestDto();
        requestDto.setNombre("Admin");
        requestDto.setProcessId(1L); // 👈 IMPORTANTE

        Process process = new Process();
        process.setId(1L);

        Role role = buildRole();

        when(processRepository.findById(1L)).thenReturn(Optional.of(process));
        when(modelMapper.map(any(RoleRequestDto.class), eq(Role.class)))
                .thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(modelMapper.map(any(Role.class), eq(RoleResponseDto.class)))
                .thenReturn(buildDto());

        RoleResponseDto result = roleService.create(requestDto);

        assertNotNull(result);
        assertEquals("Admin", result.getNombre());

        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void testDelete() {
        Role role = buildRole();

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.delete(1L);

        verify(roleRepository).delete(role);
    }
}