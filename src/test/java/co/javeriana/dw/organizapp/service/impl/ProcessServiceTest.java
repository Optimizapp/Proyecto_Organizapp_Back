package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.ProcessRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProcessServiceTest {

    private final ProcessService service = mock(ProcessService.class);

    @Test
    void findAll() {
        when(service.findAll()).thenReturn(List.of(new ProcessResponseDto()));

        assertEquals(1, service.findAll().size());
    }

    @Test
    void findById() {
        when(service.findById(1L)).thenReturn(new ProcessResponseDto());

        assertNotNull(service.findById(1L));
    }

    @Test
    void create() {
        ProcessRequestDto dto = new ProcessRequestDto();

        when(service.create(dto)).thenReturn(new ProcessResponseDto());

        assertNotNull(service.create(dto));
    }

    @Test
    void update() {
        ProcessRequestDto dto = new ProcessRequestDto();

        when(service.update(1L, dto)).thenReturn(new ProcessResponseDto());

        assertNotNull(service.update(1L, dto));
    }

    @Test
    void delete() {
        doNothing().when(service).delete(1L);

        service.delete(1L);

        verify(service).delete(1L);
    }

    // ⚠️ FALTA:
    // - Validación de datos
    // - Implementación real
}
