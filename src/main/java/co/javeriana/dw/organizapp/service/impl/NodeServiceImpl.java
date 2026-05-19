package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;
import co.javeriana.dw.organizapp.entity.GatewayType;
import co.javeriana.dw.organizapp.entity.Lane;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.NodeType;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.ResourceInUseException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.FlowRepository;
import co.javeriana.dw.organizapp.repository.LaneRepository;
import co.javeriana.dw.organizapp.repository.NodeRepository;
import co.javeriana.dw.organizapp.repository.ProcessVersionRepository;
import co.javeriana.dw.organizapp.service.NodeService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final ProcessVersionRepository processVersionRepository;
    private final LaneRepository laneRepository;
    private final FlowRepository flowRepository;
    private final ModelMapper modelMapper;

    public NodeServiceImpl(
            NodeRepository nodeRepository,
            ProcessVersionRepository processVersionRepository,
            LaneRepository laneRepository,
            FlowRepository flowRepository,
            ModelMapper modelMapper) {
        this.nodeRepository = nodeRepository;
        this.processVersionRepository = processVersionRepository;
        this.laneRepository = laneRepository;
        this.flowRepository = flowRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NodeResponseDto> findAll() {
        return nodeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NodeResponseDto> findByVersionId(Long versionId) {
        findVersion(versionId);
        return nodeRepository.findByVersionId(versionId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public NodeResponseDto findById(Long id) {
        Node node = nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nodo no encontrado con ID: " + id));
        return toDto(node);
    }

    @Override
    @Transactional
    public NodeResponseDto create(NodeRequestDto nodeDto) {
        ProcessVersion version = findVersion(nodeDto.getVersionId());
        Lane lane = findLaneIfPresent(nodeDto.getLaneId());
        validateLaneContext(version, lane);
        NodeType nodeType = parseNodeType(nodeDto.getTipo());
        GatewayType gatewayType = parseGatewayType(nodeDto.getGatewayType());
        validateGatewayRules(nodeType, gatewayType);

        Node node = new Node();
        node.setVersion(version);
        node.setTipo(nodeType);
        node.setGatewayType(gatewayType);
        node.setNombre(nodeDto.getNombre());
        node.setDescripcion(nodeDto.getDescripcion());
        node.setX(nodeDto.getX());
        node.setY(nodeDto.getY());
        node.setWidth(nodeDto.getWidth());
        node.setHeight(nodeDto.getHeight());
        node.setLane(lane);

        return toDto(nodeRepository.save(node));
    }

    @Override
    @Transactional
    public NodeResponseDto update(Long id, NodeRequestDto nodeDto) {
        Node existingNode = nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nodo no encontrado con ID: " + id));
        ProcessVersion version = findVersion(nodeDto.getVersionId());
        Lane lane = findLaneIfPresent(nodeDto.getLaneId());
        validateLaneContext(version, lane);
        NodeType nodeType = parseNodeType(nodeDto.getTipo());
        GatewayType gatewayType = parseGatewayType(nodeDto.getGatewayType());
        validateGatewayRules(nodeType, gatewayType);

        existingNode.setVersion(version);
        existingNode.setTipo(nodeType);
        existingNode.setGatewayType(gatewayType);
        existingNode.setNombre(nodeDto.getNombre());
        existingNode.setDescripcion(nodeDto.getDescripcion());
        existingNode.setX(nodeDto.getX());
        existingNode.setY(nodeDto.getY());
        existingNode.setWidth(nodeDto.getWidth());
        existingNode.setHeight(nodeDto.getHeight());
        existingNode.setLane(lane);

        return toDto(nodeRepository.save(existingNode));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Node node = nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nodo no encontrado con ID: " + id));
        if (flowRepository.existsByNodoOrigenIdOrNodoDestinoId(id, id)) {
            throw new ResourceInUseException("No se puede eliminar el nodo porque tiene flujos conectados");
        }
        nodeRepository.delete(node);
    }

    private NodeResponseDto toDto(Node node) {
        NodeResponseDto dto = modelMapper.map(node, NodeResponseDto.class);
        dto.setVersionId(node.getVersion().getId());
        dto.setTipo(node.getTipo().name());
        dto.setGatewayType(node.getGatewayType() == null ? null : node.getGatewayType().name());
        dto.setLaneId(node.getLane() == null ? null : node.getLane().getId());
        return dto;
    }

    private ProcessVersion findVersion(Long versionId) {
        return processVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version de proceso no encontrada con ID: " + versionId));
    }

    private NodeType parseNodeType(String nodeType) {
        if (nodeType == null || nodeType.isBlank()) {
            throw new BusinessRuleException("Tipo de nodo invalido: " + nodeType);
        }
        try {
            return NodeType.valueOf(nodeType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Tipo de nodo invalido: " + nodeType);
        }
    }

    private GatewayType parseGatewayType(String gatewayType) {
        if (gatewayType == null || gatewayType.isBlank()) {
            return null;
        }
        try {
            return GatewayType.valueOf(gatewayType.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessRuleException("Tipo de gateway invalido: " + gatewayType);
        }
    }

    private void validateGatewayRules(NodeType nodeType, GatewayType gatewayType) {
        boolean isGatewayType = nodeType == NodeType.GATEWAY || nodeType == NodeType.DECISION;
        if (isGatewayType && gatewayType == null) {
            throw new BusinessRuleException("gatewayType es obligatorio cuando type es GATEWAY o DECISION");
        }
        if (!isGatewayType && gatewayType != null) {
            throw new BusinessRuleException("gatewayType solo es permitido cuando type es GATEWAY o DECISION");
        }
    }

    private Lane findLaneIfPresent(Long laneId) {
        if (laneId == null) {
            return null;
        }
        return laneRepository.findById(laneId)
                .orElseThrow(() -> new ResourceNotFoundException("Lane no encontrada con ID: " + laneId));
    }

    private void validateLaneContext(ProcessVersion version, Lane lane) {
        if (lane == null) {
            return;
        }

        co.javeriana.dw.organizapp.entity.Process process = version.getProceso();
        Long processCompanyId = process.getCompany().getId();
        Long laneCompanyId = lane.getPool().getCompany().getId();
        if (!processCompanyId.equals(laneCompanyId)) {
            throw new BusinessRuleException("La lane no pertenece a la misma empresa del proceso");
        }

        if (process.getMainPool() != null && !process.getMainPool().getId().equals(lane.getPool().getId())) {
            throw new BusinessRuleException("La lane no pertenece al pool principal del proceso");
        }
    }
}
