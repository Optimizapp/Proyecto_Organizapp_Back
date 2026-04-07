package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class RoleServiceTest {

    private final RoleService service = mock(RoleService.class);

    @Test
    void findByProcessId() {
        when(service.findByProcessId(1L)).thenReturn(List.of(new RoleResponseDto()));

        assertFalse(service.findByProcessId(1L).isEmpty());
    }

    //FALTA:
    // Más métodos (create, update...)
    // ServiceImpl
}
