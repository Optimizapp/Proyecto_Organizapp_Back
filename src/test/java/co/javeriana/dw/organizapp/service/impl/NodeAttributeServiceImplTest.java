package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.NodeAttributeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeAttributeResponseDto;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.NodeAttribute;
import co.javeriana.dw.organizapp.entity.NodeAttributeType;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.NodeAttributeRepository;
import co.javeriana.dw.organizapp.repository.NodeRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NodeAttributeServiceImplTest {

    private NodeAttributeRepository nodeAttributeRepository;
    private NodeRepository nodeRepository;
    private ModelMapper modelMapper;
    private NodeAttributeServiceImpl service;

    @BeforeEach
    void setUp() {
        nodeAttributeRepository = mock(NodeAttributeRepository.class);
        nodeRepository = mock(NodeRepository.class);
        modelMapper = new ModelMapper();
        service = new NodeAttributeServiceImpl(nodeAttributeRepository, nodeRepository, modelMapper);
    }

    @Test
    void findAll() {
        Node node = new Node();
        node.setId(1L);

        NodeAttribute attr = new NodeAttribute();
        attr.setId(1L);
        attr.setNodo(node);
        attr.setTipo(NodeAttributeType.TEXTO);

        when(nodeAttributeRepository.findAll()).thenReturn(List.of(attr));

        List<NodeAttributeResponseDto> result = service.findAll();

        assertEquals(1, result.size());
    }

    @Test
    void findByNodeId() {
        Node node = new Node();
        node.setId(1L);

        NodeAttribute attr = new NodeAttribute();
        attr.setId(1L);
        attr.setNodo(node);
        attr.setTipo(NodeAttributeType.TEXTO);

        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node));
        when(nodeAttributeRepository.findByNodoId(1L)).thenReturn(List.of(attr));

        List<NodeAttributeResponseDto> result = service.findByNodeId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void findById() {
        Node node = new Node();
        node.setId(1L);

        NodeAttribute attr = new NodeAttribute();
        attr.setId(1L);
        attr.setNodo(node);
        attr.setTipo(NodeAttributeType.TEXTO);

        when(nodeAttributeRepository.findById(1L)).thenReturn(Optional.of(attr));

        NodeAttributeResponseDto result = service.findById(1L);

        assertNotNull(result);
    }

    @Test
    void findById_notFound() {
        when(nodeAttributeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create() {
        Node node = new Node();
        node.setId(1L);

        NodeAttributeRequestDto request = new NodeAttributeRequestDto();
        request.setNodeId(1L);
        request.setClave("clave");
        request.setValor("valor");
        request.setTipo("TEXTO");

        NodeAttribute attr = new NodeAttribute();
        attr.setId(1L);
        attr.setNodo(node);
        attr.setTipo(NodeAttributeType.TEXTO);

        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node));
        when(nodeAttributeRepository.save(any())).thenReturn(attr);

        NodeAttributeResponseDto result = service.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        Node node = new Node();
        node.setId(1L);

        NodeAttribute existing = new NodeAttribute();
        existing.setId(1L);
        existing.setNodo(node);
        existing.setTipo(NodeAttributeType.TEXTO);

        NodeAttributeRequestDto request = new NodeAttributeRequestDto();
        request.setNodeId(1L);
        request.setClave("clave");
        request.setValor("valor");
        request.setTipo("TEXTO");

        when(nodeAttributeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node));
        when(nodeAttributeRepository.save(any())).thenReturn(existing);

        NodeAttributeResponseDto result = service.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        NodeAttribute attr = new NodeAttribute();
        attr.setId(1L);

        when(nodeAttributeRepository.findById(1L)).thenReturn(Optional.of(attr));
        doNothing().when(nodeAttributeRepository).delete(attr);

        service.delete(1L);

        verify(nodeAttributeRepository).delete(attr);
    }

    @Test
    void delete_notFound() {
        when(nodeAttributeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}