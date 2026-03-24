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
import co.javeriana.dw.organizapp.service.FlowService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FlowServiceImpl implements FlowService {

    private final FlowRepository flowRepository;
    private final ProcessVersionRepository processVersionRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public FlowServiceImpl(
            FlowRepository flowRepository,
            ProcessVersionRepository processVersionRepository,
            NodeRepository nodeRepository,
            ModelMapper modelMapper) {
        this.flowRepository = flowRepository;
        this.processVersionRepository = processVersionRepository;
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlowResponseDto> findAll() {
        return flowRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlowResponseDto> findByVersionId(Long versionId) {
        findVersion(versionId);
        return flowRepository.findByVersionId(versionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FlowResponseDto findById(Long id) {
        Flow flow = flowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flujo no encontrado con ID: " + id));
        return toDto(flow);
    }

    @Override
    @Transactional
    public FlowResponseDto create(FlowRequestDto flowDto) {
        ProcessVersion version = findVersion(flowDto.getVersionId());
        Node originNode = findNode(flowDto.getOriginNodeId());
        Node destinationNode = findNode(flowDto.getDestinationNodeId());
        validateNodesBelongToVersion(version, originNode, destinationNode);

        Flow flow = modelMapper.map(flowDto, Flow.class);
        flow.setVersion(version);
        flow.setNodoOrigen(originNode);
        flow.setNodoDestino(destinationNode);

        return toDto(flowRepository.save(flow));
    }

    @Override
    @Transactional
    public FlowResponseDto update(Long id, FlowRequestDto flowDto) {
        Flow existingFlow = flowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flujo no encontrado con ID: " + id));
        ProcessVersion version = findVersion(flowDto.getVersionId());
        Node originNode = findNode(flowDto.getOriginNodeId());
        Node destinationNode = findNode(flowDto.getDestinationNodeId());
        validateNodesBelongToVersion(version, originNode, destinationNode);

        existingFlow.setVersion(version);
        existingFlow.setNodoOrigen(originNode);
        existingFlow.setNodoDestino(destinationNode);
        existingFlow.setCondicion(flowDto.getCondicion());
        existingFlow.setEtiqueta(flowDto.getEtiqueta());

        return toDto(flowRepository.save(existingFlow));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Flow flow = flowRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flujo no encontrado con ID: " + id));
        flowRepository.delete(flow);
    }

    private FlowResponseDto toDto(Flow flow) {
        FlowResponseDto dto = modelMapper.map(flow, FlowResponseDto.class);
        dto.setVersionId(flow.getVersion().getId());
        dto.setOriginNodeId(flow.getNodoOrigen().getId());
        dto.setDestinationNodeId(flow.getNodoDestino().getId());
        return dto;
    }

    private ProcessVersion findVersion(Long versionId) {
        return processVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version de proceso no encontrada con ID: " + versionId));
    }

    private Node findNode(Long nodeId) {
        return nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Nodo no encontrado con ID: " + nodeId));
    }

    private void validateNodesBelongToVersion(ProcessVersion version, Node originNode, Node destinationNode) {
        if (!originNode.getVersion().getId().equals(version.getId())
                || !destinationNode.getVersion().getId().equals(version.getId())) {
            throw new IllegalArgumentException("Los nodos del flujo deben pertenecer a la misma version del proceso");
        }
    }
}
