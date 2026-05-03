package co.javeriana.dw.organizapp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import co.javeriana.dw.organizapp.dto.FlowRequestDto;
import co.javeriana.dw.organizapp.dto.FlowResponseDto;
import co.javeriana.dw.organizapp.entity.Flow;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.repository.FlowRepository;
import co.javeriana.dw.organizapp.repository.NodeRepository;
import co.javeriana.dw.organizapp.repository.ProcessVersionRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

class FlowServiceImplTest {

    private FlowRepository flowRepository;
    private ProcessVersionRepository processVersionRepository;
    private NodeRepository nodeRepository;
    private FlowServiceImpl flowService;

    @BeforeEach
    void setUp() {
        flowRepository = mock(FlowRepository.class);
        processVersionRepository = mock(ProcessVersionRepository.class);
        nodeRepository = mock(NodeRepository.class);
        flowService = new FlowServiceImpl(flowRepository, processVersionRepository, nodeRepository, new ModelMapper());
    }

    @Test
    void createFlowReturnsResponseWhenNodesBelongToSameVersion() {
        ProcessVersion version = version(1L);
        Node source = node(10L, version);
        Node target = node(11L, version);
        FlowRequestDto request = flowRequest(1L, 10L, 11L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findById(10L)).thenReturn(Optional.of(source));
        when(nodeRepository.findById(11L)).thenReturn(Optional.of(target));
        when(flowRepository.save(any(Flow.class))).thenAnswer(invocation -> {
            Flow flow = invocation.getArgument(0);
            flow.setId(30L);
            return flow;
        });

        FlowResponseDto response = flowService.create(request);

        assertThat(response.getId()).isEqualTo(30L);
        assertThat(response.getType()).isEqualTo("SEQUENCE");
        assertThat(response.getOriginNodeId()).isEqualTo(10L);
        assertThat(response.getDestinationNodeId()).isEqualTo(11L);
    }

    @Test
    void createFlowRejectsSameSourceAndTarget() {
        ProcessVersion version = version(1L);
        Node source = node(10L, version);
        FlowRequestDto request = flowRequest(1L, 10L, 10L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findById(10L)).thenReturn(Optional.of(source));

        assertThatThrownBy(() -> flowService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("El nodo origen y destino no pueden ser el mismo");
    }

    @Test
    void createFlowRejectsNodesFromDifferentVersions() {
        ProcessVersion version = version(1L);
        Node source = node(10L, version);
        Node target = node(11L, version(2L));
        FlowRequestDto request = flowRequest(1L, 10L, 11L);
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findById(10L)).thenReturn(Optional.of(source));
        when(nodeRepository.findById(11L)).thenReturn(Optional.of(target));

        assertThatThrownBy(() -> flowService.create(request))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("Los nodos del flujo deben pertenecer a la misma version");
    }

    private static FlowRequestDto flowRequest(Long versionId, Long sourceId, Long targetId) {
        FlowRequestDto request = new FlowRequestDto();
        request.setVersionId(versionId);
        request.setOriginNodeId(sourceId);
        request.setDestinationNodeId(targetId);
        request.setEtiqueta("Siguiente");
        return request;
    }

    private static ProcessVersion version(Long id) {
        ProcessVersion version = new ProcessVersion();
        version.setId(id);
        return version;
    }

    private static Node node(Long id, ProcessVersion version) {
        Node node = new Node();
        node.setId(id);
        node.setVersion(version);
        return node;
    }
}
