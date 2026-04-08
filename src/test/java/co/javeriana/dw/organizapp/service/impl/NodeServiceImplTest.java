package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.NodeType;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.NodeRepository;
import co.javeriana.dw.organizapp.repository.ProcessVersionRepository;
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
class NodeServiceImplTest {

    @Mock
    private NodeRepository nodeRepository;

    @Mock
    private ProcessVersionRepository processVersionRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private NodeServiceImpl service;

    @Test
    void shouldFindNodesByVersionId() {
        ProcessVersion version = buildVersion(1L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findByVersionId(1L)).thenReturn(List.of(buildNode(10L, version, NodeType.TAREA)));

        List<NodeResponseDto> result = service.findByVersionId(1L);

        assertEquals(1, result.size());
        assertEquals("TAREA", result.get(0).getTipo());
    }

    @Test
    void shouldCreateNodeWithParsedType() {
        NodeRequestDto request = buildRequest();
        ProcessVersion version = buildVersion(1L);
        Node saved = buildNode(5L, version, NodeType.INICIO);

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.save(any(Node.class))).thenReturn(saved);

        NodeResponseDto result = service.create(request);

        assertEquals(1L, result.getVersionId());
        assertEquals("INICIO", result.getTipo());
    }

    @Test
    void shouldRejectInvalidNodeType() {
        NodeRequestDto request = buildRequest();
        request.setTipo("desconocido");
        ProcessVersion version = buildVersion(1L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.create(request));

        assertEquals("Tipo de nodo invalido: desconocido", exception.getMessage());
    }

    @Test
    void shouldUpdateNode() {
        NodeRequestDto request = buildRequest();
        ProcessVersion version = buildVersion(1L);
        Node existing = buildNode(5L, version, NodeType.TAREA);
        Node updated = buildNode(5L, version, NodeType.INICIO);
        updated.setNombre("Nodo inicial");

        when(nodeRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.save(existing)).thenReturn(updated);

        NodeResponseDto result = service.update(5L, request);

        assertEquals("INICIO", result.getTipo());
    }

    @Test
    void shouldDeleteNode() {
        Node existing = buildNode(5L, buildVersion(1L), NodeType.TAREA);
        when(nodeRepository.findById(5L)).thenReturn(Optional.of(existing));

        service.delete(5L);

        verify(nodeRepository).delete(existing);
    }

    @Test
    void shouldThrowWhenNodeNotFound() {
        when(nodeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    private NodeRequestDto buildRequest() {
        NodeRequestDto request = new NodeRequestDto();
        request.setVersionId(1L);
        request.setTipo("inicio");
        request.setNombre("Nodo inicial");
        request.setDescripcion("Descripcion");
        request.setX(10F);
        request.setY(20F);
        return request;
    }

    private ProcessVersion buildVersion(Long id) {
        ProcessVersion version = new ProcessVersion();
        version.setId(id);
        return version;
    }

    private Node buildNode(Long id, ProcessVersion version, NodeType type) {
        Node node = new Node();
        node.setId(id);
        node.setVersion(version);
        node.setTipo(type);
        node.setNombre("Nodo");
        node.setDescripcion("Descripcion");
        node.setX(10F);
        node.setY(20F);
        return node;
    }
}
