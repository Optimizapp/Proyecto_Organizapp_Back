package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
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
    private ModelMapper modelMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void testFindAll() {
        Role role = new Role();
        role.setId(1L);
        role.setNombre("Admin");

        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(1L);
        dto.setNombre("Admin");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role));
        when(modelMapper.map(any(Role.class), eq(RoleResponseDto.class))).thenReturn(dto);

        List<RoleResponseDto> result = roleService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Admin", result.get(0).getNombre());
    }

    @Test
    void testFindByIdSuccess() {
        Role role = new Role();
        role.setId(1L);
        role.setNombre("Admin");

        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(1L);
        dto.setNombre("Admin");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(modelMapper.map(any(Role.class), eq(RoleResponseDto.class))).thenReturn(dto);

        RoleResponseDto result = roleService.findById(1L);

        assertNotNull(result);
        assertEquals("Admin", result.getNombre());
    }

    @Test
    void testFindByIdNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.findById(1L));
    }

    @Test
    void testCreate() {
        RoleRequestDto requestDto = new RoleRequestDto();
        requestDto.setNombre("Admin");

        Role role = new Role();
        role.setId(1L);
        role.setNombre("Admin");

        RoleResponseDto responseDto = new RoleResponseDto();
        responseDto.setId(1L);
        responseDto.setNombre("Admin");

        when(modelMapper.map(any(RoleRequestDto.class), eq(Role.class))).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(modelMapper.map(any(Role.class), eq(RoleResponseDto.class))).thenReturn(responseDto);

        RoleResponseDto result = roleService.create(requestDto);

        assertNotNull(result);
        assertEquals("Admin", result.getNombre());
    }

    @Test
    void testDelete() {
        Role role = new Role();
        role.setId(1L);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).delete(any(Role.class));

        assertDoesNotThrow(() -> roleService.delete(1L));
        verify(roleRepository, times(1)).delete(any(Role.class));
    }
}
