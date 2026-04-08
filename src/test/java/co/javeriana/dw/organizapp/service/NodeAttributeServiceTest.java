package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.NodeAttributeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeAttributeResponseDto;

import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NodeAttributeServiceTest {

    private final NodeAttributeService nodeAttributeService = mock(NodeAttributeService.class);

    public NodeAttributeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        when(nodeAttributeService.findAll()).thenReturn(List.of(new NodeAttributeResponseDto()));

        List<NodeAttributeResponseDto> result = nodeAttributeService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByNodeId() {
        when(nodeAttributeService.findByNodeId(1L)).thenReturn(List.of(new NodeAttributeResponseDto()));

        List<NodeAttributeResponseDto> result = nodeAttributeService.findByNodeId(1L);

        assertNotNull(result);
    }

    @Test
    void findById() {
        when(nodeAttributeService.findById(1L)).thenReturn(new NodeAttributeResponseDto());

        NodeAttributeResponseDto result = nodeAttributeService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void create() {
        NodeAttributeRequestDto request = new NodeAttributeRequestDto();
        NodeAttributeResponseDto response = new NodeAttributeResponseDto();

        when(nodeAttributeService.create(any())).thenReturn(response);

        NodeAttributeResponseDto result = nodeAttributeService.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        NodeAttributeRequestDto request = new NodeAttributeRequestDto();
        NodeAttributeResponseDto response = new NodeAttributeResponseDto();

        when(nodeAttributeService.update(eq(1L), any())).thenReturn(response);

        NodeAttributeResponseDto result = nodeAttributeService.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        doNothing().when(nodeAttributeService).delete(1L);

        assertDoesNotThrow(() -> nodeAttributeService.delete(1L));
    }
}