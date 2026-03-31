package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.NodeAttributeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeAttributeResponseDto;
import co.javeriana.dw.organizapp.entity.Node;
import co.javeriana.dw.organizapp.entity.NodeAttribute;
import co.javeriana.dw.organizapp.entity.NodeAttributeType;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.NodeAttributeRepository;
import co.javeriana.dw.organizapp.repository.NodeRepository;
import co.javeriana.dw.organizapp.service.NodeAttributeService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NodeAttributeServiceImpl implements NodeAttributeService {

    private static final String NODE_ATTRIBUTE_NOT_FOUND_MESSAGE = "Atributo de nodo no encontrado con ID: ";

    private final NodeAttributeRepository nodeAttributeRepository;
    private final NodeRepository nodeRepository;
    private final ModelMapper modelMapper;

    public NodeAttributeServiceImpl(
            NodeAttributeRepository nodeAttributeRepository,
            NodeRepository nodeRepository,
            ModelMapper modelMapper) {
        this.nodeAttributeRepository = nodeAttributeRepository;
        this.nodeRepository = nodeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NodeAttributeResponseDto> findAll() {
        return nodeAttributeRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<NodeAttributeResponseDto> findByNodeId(Long nodeId) {
        findNode(nodeId);
        return nodeAttributeRepository.findByNodoId(nodeId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NodeAttributeResponseDto findById(Long id) {
        NodeAttribute attribute = nodeAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NODE_ATTRIBUTE_NOT_FOUND_MESSAGE + id));
        return toDto(attribute);
    }

    @Override
    @Transactional
    public NodeAttributeResponseDto create(NodeAttributeRequestDto nodeAttributeDto) {
        Node node = findNode(nodeAttributeDto.getNodeId());

        NodeAttribute attribute = modelMapper.map(nodeAttributeDto, NodeAttribute.class);
        attribute.setNodo(node);
        attribute.setTipo(parseAttributeType(nodeAttributeDto.getTipo()));

        return toDto(nodeAttributeRepository.save(attribute));
    }

    @Override
    @Transactional
    public NodeAttributeResponseDto update(Long id, NodeAttributeRequestDto nodeAttributeDto) {
        NodeAttribute existingAttribute = nodeAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NODE_ATTRIBUTE_NOT_FOUND_MESSAGE + id));
        Node node = findNode(nodeAttributeDto.getNodeId());

        existingAttribute.setNodo(node);
        existingAttribute.setClave(nodeAttributeDto.getClave());
        existingAttribute.setValor(nodeAttributeDto.getValor());
        existingAttribute.setTipo(parseAttributeType(nodeAttributeDto.getTipo()));

        return toDto(nodeAttributeRepository.save(existingAttribute));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        NodeAttribute attribute = nodeAttributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NODE_ATTRIBUTE_NOT_FOUND_MESSAGE + id));
        nodeAttributeRepository.delete(attribute);
    }

    private NodeAttributeResponseDto toDto(NodeAttribute attribute) {
        NodeAttributeResponseDto dto = modelMapper.map(attribute, NodeAttributeResponseDto.class);
        dto.setNodeId(attribute.getNodo().getId());
        dto.setTipo(attribute.getTipo().name());
        return dto;
    }

    private Node findNode(Long nodeId) {
        return nodeRepository.findById(nodeId)
                .orElseThrow(() -> new ResourceNotFoundException("Nodo no encontrado con ID: " + nodeId));
    }

    private NodeAttributeType parseAttributeType(String attributeType) {
        try {
            return NodeAttributeType.valueOf(attributeType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IllegalArgumentException("Tipo de atributo invalido: " + attributeType);
        }
    }
}
