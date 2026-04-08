package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.PermissionRequestDto;
import co.javeriana.dw.organizapp.dto.PermissionResponseDto;
import co.javeriana.dw.organizapp.entity.Permission;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.PermissionRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceImplTest {

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private PermissionServiceImpl service;

    @Test
    void shouldFindByRoleId() {
        Role role = buildRole(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findByRolId(1L)).thenReturn(List.of(buildPermission(2L, role)));

        List<PermissionResponseDto> result = service.findByRoleId(1L);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getRoleId());
    }

    @Test
    void shouldCreatePermission() {
        Role role = buildRole(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.save(any(Permission.class))).thenReturn(buildPermission(2L, role));

        PermissionResponseDto result = service.create(buildRequest());

        assertEquals("READ", result.getCodigo());
        assertEquals(1L, result.getRoleId());
    }

    @Test
    void shouldUpdatePermission() {
        Role role = buildRole(1L);
        Permission existing = buildPermission(2L, role);
        when(permissionRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.save(existing)).thenReturn(existing);

        PermissionResponseDto result = service.update(2L, buildRequest());

        assertEquals("READ", result.getCodigo());
    }

    @Test
    void shouldDeletePermission() {
        Permission existing = buildPermission(2L, buildRole(1L));
        when(permissionRepository.findById(2L)).thenReturn(Optional.of(existing));

        service.delete(2L);

        verify(permissionRepository).delete(existing);
    }

    @Test
    void shouldThrowWhenPermissionNotFound() {
        when(permissionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    private PermissionRequestDto buildRequest() {
        PermissionRequestDto request = new PermissionRequestDto();
        request.setCodigo("READ");
        request.setDescripcion("Lectura");
        request.setRoleId(1L);
        return request;
    }

    private Role buildRole(Long id) {
        Role role = new Role();
        role.setId(id);
        return role;
    }

    private Permission buildPermission(Long id, Role role) {
        Permission permission = new Permission();
        permission.setId(id);
        permission.setCodigo("READ");
        permission.setDescripcion("Lectura");
        permission.setRol(role);
        return permission;
    }
}
