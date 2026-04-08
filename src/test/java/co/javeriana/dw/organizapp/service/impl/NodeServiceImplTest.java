package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.NodeType;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.NodeRepository;
import co.javeriana.dw.organizapp.repository.ProcessVersionRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NodeServiceImplTest {

    private NodeRepository nodeRepository;
    private ProcessVersionRepository processVersionRepository;
    private ModelMapper modelMapper;
    private NodeServiceImpl service;

    @BeforeEach
    void setUp() {
        nodeRepository = mock(NodeRepository.class);
        processVersionRepository = mock(ProcessVersionRepository.class);
        modelMapper = new ModelMapper();
        service = new NodeServiceImpl(nodeRepository, processVersionRepository, modelMapper);
    }

    @Test
    void findAll() {
        ProcessVersion version = new ProcessVersion();
        version.setId(1L);

        Node node = new Node();
        node.setId(1L);
        node.setVersion(version);
        node.setTipo(NodeType.INICIO);

        when(nodeRepository.findAll()).thenReturn(List.of(node));

        List<NodeResponseDto> result = service.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findByVersionId() {
        ProcessVersion version = new ProcessVersion();
        version.setId(1L);

        Node node = new Node();
        node.setId(1L);
        node.setVersion(version);
        node.setTipo(NodeType.INICIO);

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findByVersionId(1L)).thenReturn(List.of(node));

        List<NodeResponseDto> result = service.findByVersionId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void findById() {
        ProcessVersion version = new ProcessVersion();
        version.setId(1L);

        Node node = new Node();
        node.setId(1L);
        node.setVersion(version);
        node.setTipo(NodeType.INICIO);

        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node));

        NodeResponseDto result = service.findById(1L);

        assertNotNull(result);
    }

    @Test
    void findById_notFound() {
        when(nodeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create() {
        ProcessVersion version = new ProcessVersion();
        version.setId(1L);

        NodeRequestDto request = new NodeRequestDto();
        request.setVersionId(1L);
        request.setTipo("INICIO");
        request.setNombre("Nodo");
        request.setDescripcion("Desc");
        request.setX(1f);
        request.setY(1f);

        Node node = new Node();
        node.setId(1L);
        node.setVersion(version);
        node.setTipo(NodeType.INICIO);

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.save(any())).thenReturn(node);

        NodeResponseDto result = service.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        ProcessVersion version = new ProcessVersion();
        version.setId(1L);

        Node existing = new Node();
        existing.setId(1L);
        existing.setVersion(version);
        existing.setTipo(NodeType.INICIO);

        NodeRequestDto request = new NodeRequestDto();
        request.setVersionId(1L);
        request.setTipo("INICIO");
        request.setNombre("Nodo");
        request.setDescripcion("Desc");
        request.setX(1f);
        request.setY(1f);

        when(nodeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.save(any())).thenReturn(existing);

        NodeResponseDto result = service.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        Node node = new Node();
        node.setId(1L);

        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node));
        doNothing().when(nodeRepository).delete(node);

        service.delete(1L);

        verify(nodeRepository).delete(node);
    }

    @Test
    void delete_notFound() {
        when(nodeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}