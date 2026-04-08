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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlowServiceImplTest {

    private FlowRepository flowRepository;
    private ProcessVersionRepository processVersionRepository;
    private NodeRepository nodeRepository;
    private ModelMapper modelMapper;

    private FlowServiceImpl service;

    @BeforeEach
    void setUp() {
        flowRepository = mock(FlowRepository.class);
        processVersionRepository = mock(ProcessVersionRepository.class);
        nodeRepository = mock(NodeRepository.class);
        modelMapper = mock(ModelMapper.class);

        service = new FlowServiceImpl(
                flowRepository,
                processVersionRepository,
                nodeRepository,
                modelMapper
        );
    }

    private ProcessVersion mockVersion(Long id) {
        ProcessVersion v = new ProcessVersion();
        v.setId(id);
        return v;
    }

    private Node mockNode(Long id, ProcessVersion version) {
        Node n = new Node();
        n.setId(id);
        n.setVersion(version);
        return n;
    }

    @Test
    void findAll() {
        Flow flow = new Flow();
        flow.setVersion(mockVersion(1L));
        flow.setNodoOrigen(mockNode(1L, flow.getVersion()));
        flow.setNodoDestino(mockNode(2L, flow.getVersion()));

        when(flowRepository.findAll()).thenReturn(List.of(flow));
        when(modelMapper.map(any(Flow.class), eq(FlowResponseDto.class)))
                .thenReturn(new FlowResponseDto());

        List<FlowResponseDto> result = service.findAll();

        assertNotNull(result);
    }

    @Test
    void findByVersionId() {
        ProcessVersion version = mockVersion(1L);

        Flow flow = new Flow();
        flow.setVersion(version);
        flow.setNodoOrigen(mockNode(1L, version));
        flow.setNodoDestino(mockNode(2L, version));

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(flowRepository.findByVersionId(1L)).thenReturn(List.of(flow));
        when(modelMapper.map(any(Flow.class), eq(FlowResponseDto.class)))
                .thenReturn(new FlowResponseDto());

        List<FlowResponseDto> result = service.findByVersionId(1L);

        assertNotNull(result);
    }

    @Test
    void findById() {
        Flow flow = new Flow();
        flow.setVersion(mockVersion(1L));
        flow.setNodoOrigen(mockNode(1L, flow.getVersion()));
        flow.setNodoDestino(mockNode(2L, flow.getVersion()));

        when(flowRepository.findById(1L)).thenReturn(Optional.of(flow));
        when(modelMapper.map(any(Flow.class), eq(FlowResponseDto.class)))
                .thenReturn(new FlowResponseDto());

        FlowResponseDto result = service.findById(1L);

        assertNotNull(result);
    }

    @Test
    void findById_notFound() {
        when(flowRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create() {
        FlowRequestDto request = new FlowRequestDto();
        request.setVersionId(1L);
        request.setOriginNodeId(1L);
        request.setDestinationNodeId(2L);
        request.setCondicion("cond");
        request.setEtiqueta("etq");

        ProcessVersion version = mockVersion(1L);
        Node origin = mockNode(1L, version);
        Node dest = mockNode(2L, version);

        Flow mapped = new Flow();
        Flow saved = new Flow();
        saved.setVersion(version);
        saved.setNodoOrigen(origin);
        saved.setNodoDestino(dest);

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(nodeRepository.findById(2L)).thenReturn(Optional.of(dest));
        when(modelMapper.map(request, Flow.class)).thenReturn(mapped);
        when(flowRepository.save(any())).thenReturn(saved);
        when(modelMapper.map(any(Flow.class), eq(FlowResponseDto.class)))
                .thenReturn(new FlowResponseDto());

        FlowResponseDto result = service.create(request);

        assertNotNull(result);
    }

    @Test
    void create_invalidNodes() {
        FlowRequestDto request = new FlowRequestDto();
        request.setVersionId(1L);
        request.setOriginNodeId(1L);
        request.setDestinationNodeId(2L);

        ProcessVersion version1 = mockVersion(1L);
        ProcessVersion version2 = mockVersion(2L);

        Node origin = mockNode(1L, version1);
        Node dest = mockNode(2L, version2);

        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version1));
        when(nodeRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(nodeRepository.findById(2L)).thenReturn(Optional.of(dest));

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    @Test
    void update() {
        FlowRequestDto request = new FlowRequestDto();
        request.setVersionId(1L);
        request.setOriginNodeId(1L);
        request.setDestinationNodeId(2L);
        request.setCondicion("cond");
        request.setEtiqueta("etq");

        ProcessVersion version = mockVersion(1L);
        Node origin = mockNode(1L, version);
        Node dest = mockNode(2L, version);

        Flow existing = new Flow();
        existing.setId(1L);

        when(flowRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(processVersionRepository.findById(1L)).thenReturn(Optional.of(version));
        when(nodeRepository.findById(1L)).thenReturn(Optional.of(origin));
        when(nodeRepository.findById(2L)).thenReturn(Optional.of(dest));
        when(flowRepository.save(any())).thenReturn(existing);
        when(modelMapper.map(any(Flow.class), eq(FlowResponseDto.class)))
                .thenReturn(new FlowResponseDto());

        FlowResponseDto result = service.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        Flow flow = new Flow();
        flow.setId(1L);

        when(flowRepository.findById(1L)).thenReturn(Optional.of(flow));
        doNothing().when(flowRepository).delete(flow);

        assertDoesNotThrow(() -> service.delete(1L));
    }

    @Test
    void delete_notFound() {
        when(flowRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}