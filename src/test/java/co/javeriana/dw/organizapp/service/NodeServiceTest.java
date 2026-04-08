package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;

import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class NodeServiceTest {

    private final NodeService nodeService = mock(NodeService.class);

    public NodeServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        when(nodeService.findAll()).thenReturn(List.of(new NodeResponseDto()));

        List<NodeResponseDto> result = nodeService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByVersionId() {
        when(nodeService.findByVersionId(1L)).thenReturn(List.of(new NodeResponseDto()));

        List<NodeResponseDto> result = nodeService.findByVersionId(1L);

        assertNotNull(result);
    }

    @Test
    void findById() {
        when(nodeService.findById(1L)).thenReturn(new NodeResponseDto());

        NodeResponseDto result = nodeService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void create() {
        NodeRequestDto request = new NodeRequestDto();
        NodeResponseDto response = new NodeResponseDto();

        when(nodeService.create(any())).thenReturn(response);

        NodeResponseDto result = nodeService.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        NodeRequestDto request = new NodeRequestDto();
        NodeResponseDto response = new NodeResponseDto();

        when(nodeService.update(eq(1L), any())).thenReturn(response);

        NodeResponseDto result = nodeService.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        doNothing().when(nodeService).delete(1L);

        assertDoesNotThrow(() -> nodeService.delete(1L));
    }
}