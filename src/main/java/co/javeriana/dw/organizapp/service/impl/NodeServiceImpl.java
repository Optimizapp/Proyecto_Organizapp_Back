package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.NodeType;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
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
    private final ModelMapper modelMapper;

    public NodeServiceImpl(
            NodeRepository nodeRepository,
            ProcessVersionRepository processVersionRepository,
            ModelMapper modelMapper) {
        this.nodeRepository = nodeRepository;
        this.processVersionRepository = processVersionRepository;
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

        Node node = modelMapper.map(nodeDto, Node.class);
        node.setVersion(version);
        node.setTipo(parseNodeType(nodeDto.getTipo()));

        return toDto(nodeRepository.save(node));
    }

    @Override
    @Transactional
    public NodeResponseDto update(Long id, NodeRequestDto nodeDto) {
        Node existingNode = nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nodo no encontrado con ID: " + id));
        ProcessVersion version = findVersion(nodeDto.getVersionId());

        existingNode.setVersion(version);
        existingNode.setTipo(parseNodeType(nodeDto.getTipo()));
        existingNode.setNombre(nodeDto.getNombre());
        existingNode.setDescripcion(nodeDto.getDescripcion());
        existingNode.setX(nodeDto.getX());
        existingNode.setY(nodeDto.getY());

        return toDto(nodeRepository.save(existingNode));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Node node = nodeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nodo no encontrado con ID: " + id));
        nodeRepository.delete(node);
    }

    private NodeResponseDto toDto(Node node) {
        NodeResponseDto dto = modelMapper.map(node, NodeResponseDto.class);
        dto.setVersionId(node.getVersion().getId());
        dto.setTipo(node.getTipo().name());
        return dto;
    }

    private ProcessVersion findVersion(Long versionId) {
        return processVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Version de proceso no encontrada con ID: " + versionId));
    }

    private NodeType parseNodeType(String nodeType) {
        try {
            return NodeType.valueOf(nodeType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Tipo de nodo invalido: " + nodeType);
        }
    }
}
