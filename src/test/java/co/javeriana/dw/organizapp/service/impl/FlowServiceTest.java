package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.FlowResponseDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class FlowServiceTest {

    private final FlowService service = mock(FlowService.class);

    @Test
    void findByVersionId() {
        when(service.findByVersionId(1L)).thenReturn(List.of(new FlowResponseDto()));

        assertEquals(1, service.findByVersionId(1L).size());
    }

    // FALTA:
    // CRUD completo
}
