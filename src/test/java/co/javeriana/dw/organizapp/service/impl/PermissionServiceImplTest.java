package co.javeriana.dw.organizapp.service.impl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.modelmapper.ModelMapper;

import co.javeriana.dw.organizapp.dto.PermissionRequestDto;
import co.javeriana.dw.organizapp.dto.PermissionResponseDto;
import co.javeriana.dw.organizapp.entity.Permission;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.PermissionRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;

class PermissionServiceImplTest {

    private PermissionRepository permissionRepository;
    private RoleRepository roleRepository;
    private ModelMapper modelMapper;
    private PermissionServiceImpl service;

    @BeforeEach
    void setUp() {
        permissionRepository = mock(PermissionRepository.class);
        roleRepository = mock(RoleRepository.class);
        modelMapper = new ModelMapper();
        service = new PermissionServiceImpl(permissionRepository, roleRepository, modelMapper);
    }

    @Test
    void findAll() {
        Role role = new Role();
        role.setId(1L);

        Permission permission = new Permission();
        permission.setId(1L);
        permission.setCodigo("READ");
        permission.setDescripcion("Read permission");
        permission.setRol(role);

        when(permissionRepository.findAll()).thenReturn(List.of(permission));

        List<PermissionResponseDto> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals("READ", result.get(0).getCodigo());
    }

    @Test
    void findByRoleId_success() {
        Role role = new Role();
        role.setId(1L);

        Permission permission = new Permission();
        permission.setId(1L);
        permission.setCodigo("WRITE");
        permission.setDescripcion("Write permission");
        permission.setRol(role);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findByRolId(1L)).thenReturn(List.of(permission));

        List<PermissionResponseDto> result = service.findByRoleId(1L);

        assertEquals(1, result.size());
        assertEquals("WRITE", result.get(0).getCodigo());
    }

    @Test
    void findByRoleId_notFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findByRoleId(1L));
    }

    @Test
    void findById_success() {
        Role role = new Role();
        role.setId(1L);

        Permission permission = new Permission();
        permission.setId(1L);
        permission.setCodigo("READ");
        permission.setDescripcion("Read permission");
        permission.setRol(role);

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        PermissionResponseDto result = service.findById(1L);

        assertEquals("READ", result.getCodigo());
    }

    @Test
    void findById_notFound() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_success() {
        Role role = new Role();
        role.setId(1L);

        PermissionRequestDto request = new PermissionRequestDto();
        request.setCodigo("CREATE");
        request.setDescripcion("Create permission");
        request.setRoleId(1L);

        Permission permission = new Permission();
        permission.setCodigo("CREATE");
        permission.setDescripcion("Create permission");
        permission.setRol(role);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        PermissionResponseDto result = service.create(request);

        assertEquals("CREATE", result.getCodigo());
    }

    @Test
    void create_roleNotFound() {
        PermissionRequestDto request = new PermissionRequestDto();
        request.setRoleId(1L);

        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(request));
    }

    @Test
    void update_success() {
        Role role = new Role();
        role.setId(1L);

        Permission existing = new Permission();
        existing.setId(1L);
        existing.setCodigo("OLD");
        existing.setDescripcion("Old desc");
        existing.setRol(role);

        PermissionRequestDto request = new PermissionRequestDto();
        request.setCodigo("NEW");
        request.setDescripcion("New desc");
        request.setRoleId(1L);

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.save(any(Permission.class))).thenReturn(existing);

        PermissionResponseDto result = service.update(1L, request);

        assertEquals("NEW", result.getCodigo());
    }

    @Test
    void update_permissionNotFound() {
        PermissionRequestDto request = new PermissionRequestDto();
        request.setRoleId(1L);

        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, request));
    }

    @Test
    void delete_success() {
        Permission permission = new Permission();
        permission.setId(1L);

        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        service.delete(1L);

        verify(permissionRepository).delete(permission);
    }

    @Test
    void delete_notFound() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}