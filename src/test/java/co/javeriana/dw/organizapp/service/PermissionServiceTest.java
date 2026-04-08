package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.PermissionRequestDto;
import co.javeriana.dw.organizapp.dto.PermissionResponseDto;

import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PermissionServiceTest {

    private final PermissionService permissionService = mock(PermissionService.class);

    public PermissionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        when(permissionService.findAll()).thenReturn(List.of(new PermissionResponseDto()));

        List<PermissionResponseDto> result = permissionService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByRoleId() {
        when(permissionService.findByRoleId(1L)).thenReturn(List.of(new PermissionResponseDto()));

        List<PermissionResponseDto> result = permissionService.findByRoleId(1L);

        assertNotNull(result);
    }

    @Test
    void findById() {
        when(permissionService.findById(1L)).thenReturn(new PermissionResponseDto());

        PermissionResponseDto result = permissionService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void create() {
        PermissionRequestDto request = new PermissionRequestDto();
        PermissionResponseDto response = new PermissionResponseDto();

        when(permissionService.create(any())).thenReturn(response);

        PermissionResponseDto result = permissionService.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        PermissionRequestDto request = new PermissionRequestDto();
        PermissionResponseDto response = new PermissionResponseDto();

        when(permissionService.update(eq(1L), any())).thenReturn(response);

        PermissionResponseDto result = permissionService.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        doNothing().when(permissionService).delete(1L);

        assertDoesNotThrow(() -> permissionService.delete(1L));
    }
}