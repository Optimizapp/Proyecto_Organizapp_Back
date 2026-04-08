package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.FlowRequestDto;
import co.javeriana.dw.organizapp.dto.FlowResponseDto;
import co.javeriana.dw.organizapp.entity.Flow;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.FlowRepository;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlowServiceImplTest {

    @Mock
    private FlowRepository flowRepository;

    @Mock
    private ProcessVersionRepository processVersionRepository;

    @Mock
    private NodeRepository nodeRepository;

    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private FlowServiceImpl service;

    @Test
    void shouldFindAllFlows() {
        Flow flow = buildFlow(1L, 10L, 20L);
        when(flowRepository.findAll()).thenReturn(List.of(flow));

        List<FlowResponseDto> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getVersionId());
        assertEquals(10L, result.get(0).getOriginNodeId());
        assertEquals(20L, result.get(0).getDestinationNodeId());
    }

    @Test
    void shouldCreateFlowWhenNodesBelongToVersion() {
        FlowRequestDto request = buildRequest(1L, 10L, 20L);
        ProcessVersion version = buildVersion(1L);
        Node originNode = buildNode(10L, version);
        Node destinationNode = buildNode(20L, version);
        Flow saved = buildFlow(1L, 10L, 20L);

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findById(10L)).thenReturn(Optional.of(originNode));
        when(nodeRepository.findById(20L)).thenReturn(Optional.of(destinationNode));
        when(flowRepository.save(any(Flow.class))).thenReturn(saved);

        FlowResponseDto result = service.create(request);

        assertEquals(1L, result.getVersionId());
        assertEquals(10L, result.getOriginNodeId());
        assertEquals(20L, result.getDestinationNodeId());
    }

    @Test
    void shouldRejectCreateWhenNodeBelongsToDifferentVersion() {
        FlowRequestDto request = buildRequest(1L, 10L, 20L);
        ProcessVersion version = buildVersion(1L);
        Node originNode = buildNode(10L, version);
        Node destinationNode = buildNode(20L, buildVersion(2L));

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findById(10L)).thenReturn(Optional.of(originNode));
        when(nodeRepository.findById(20L)).thenReturn(Optional.of(destinationNode));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> service.create(request));

        assertTrue(exception.getMessage().contains("misma version"));
    }

    @Test
    void shouldThrowWhenFlowNotFoundById() {
        when(flowRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void shouldDeleteExistingFlow() {
        Flow flow = buildFlow(1L, 10L, 20L);
        when(flowRepository.findById(5L)).thenReturn(Optional.of(flow));

        service.delete(5L);

        verify(flowRepository).delete(flow);
    }

    private FlowRequestDto buildRequest(Long versionId, Long originNodeId, Long destinationNodeId) {
        FlowRequestDto request = new FlowRequestDto();
        request.setVersionId(versionId);
        request.setOriginNodeId(originNodeId);
        request.setDestinationNodeId(destinationNodeId);
        request.setCondicion("ok");
        request.setEtiqueta("etiqueta");
        return request;
    }

    private Flow buildFlow(Long versionId, Long originNodeId, Long destinationNodeId) {
        ProcessVersion version = buildVersion(versionId);
        Node originNode = buildNode(originNodeId, version);
        Node destinationNode = buildNode(destinationNodeId, version);

        Flow flow = new Flow();
        flow.setId(30L);
        flow.setVersion(version);
        flow.setNodoOrigen(originNode);
        flow.setNodoDestino(destinationNode);
        flow.setCondicion("ok");
        flow.setEtiqueta("etiqueta");
        return flow;
    }

    private ProcessVersion buildVersion(Long id) {
        ProcessVersion version = new ProcessVersion();
        version.setId(id);
        return version;
    }

    private Node buildNode(Long id, ProcessVersion version) {
        Node node = new Node();
        node.setId(id);
        node.setVersion(version);
        return node;
    }
}
