package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.NodeAttributeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeAttributeResponseDto;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.NodeAttribute;
import co.javeriana.dw.organizapp.entity.NodeAttributeType;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.NodeAttributeRepository;
import co.javeriana.dw.organizapp.repository.NodeRepository;
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
class NodeAttributeServiceImplTest {

    @Mock
    private NodeAttributeRepository nodeAttributeRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private NodeAttributeServiceImpl service;

    @Test
    void shouldFindAttributesByNodeId() {
        Node node = buildNode(1L);
        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node));
        when(nodeAttributeRepository.findByNodoId(1L)).thenReturn(List.of(buildAttribute(2L, node, NodeAttributeType.TEXTO)));

        List<NodeAttributeResponseDto> result = service.findByNodeId(1L);

        assertEquals(1, result.size());
        assertEquals("TEXTO", result.get(0).getTipo());
    }

    @Test
    void shouldCreateAttribute() {
        Node node = buildNode(1L);
        NodeAttributeRequestDto request = buildRequest();
        NodeAttribute saved = buildAttribute(2L, node, NodeAttributeType.NUMERO);

        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node));
        when(nodeAttributeRepository.save(any(NodeAttribute.class))).thenReturn(saved);

        NodeAttributeResponseDto result = service.create(request);

        assertEquals(1L, result.getNodeId());
        assertEquals("NUMERO", result.getTipo());
    }

    @Test
    void shouldRejectInvalidAttributeType() {
        NodeAttributeRequestDto request = buildRequest();
        request.setTipo("raro");
        when(nodeRepository.findById(1L)).thenReturn(Optional.of(buildNode(1L)));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.create(request));

        assertEquals("Tipo de atributo invalido: raro", exception.getMessage());
    }

    @Test
    void shouldUpdateAttribute() {
        Node node = buildNode(1L);
        NodeAttribute existing = buildAttribute(2L, node, NodeAttributeType.TEXTO);
        NodeAttribute saved = buildAttribute(2L, node, NodeAttributeType.JSON);

        when(nodeAttributeRepository.findById(2L)).thenReturn(Optional.of(existing));
        when(nodeRepository.findById(1L)).thenReturn(Optional.of(node));
        when(nodeAttributeRepository.save(existing)).thenReturn(saved);

        NodeAttributeResponseDto result = service.update(2L, buildRequest());

        assertEquals("JSON", result.getTipo());
    }

    @Test
    void shouldDeleteAttribute() {
        NodeAttribute existing = buildAttribute(2L, buildNode(1L), NodeAttributeType.TEXTO);
        when(nodeAttributeRepository.findById(2L)).thenReturn(Optional.of(existing));

        service.delete(2L);

        verify(nodeAttributeRepository).delete(existing);
    }

    @Test
    void shouldThrowWhenAttributeNotFound() {
        when(nodeAttributeRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(2L));
    }

    private NodeAttributeRequestDto buildRequest() {
        NodeAttributeRequestDto request = new NodeAttributeRequestDto();
        request.setNodeId(1L);
        request.setClave("prioridad");
        request.setValor("1");
        request.setTipo("json");
        return request;
    }

    private Node buildNode(Long id) {
        Node node = new Node();
        node.setId(id);
        return node;
    }

    private NodeAttribute buildAttribute(Long id, Node node, NodeAttributeType type) {
        NodeAttribute attribute = new NodeAttribute();
        attribute.setId(id);
        attribute.setNodo(node);
        attribute.setClave("clave");
        attribute.setValor("valor");
        attribute.setTipo(type);
        return attribute;
    }
}
