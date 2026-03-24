package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.NodeAttributeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeAttributeResponseDto;
import java.util.List;

public interface NodeAttributeService {
    List<NodeAttributeResponseDto> findAll();
    List<NodeAttributeResponseDto> findByNodeId(Long nodeId);
    NodeAttributeResponseDto findById(Long id);
    NodeAttributeResponseDto create(NodeAttributeRequestDto nodeAttributeDto);
    NodeAttributeResponseDto update(Long id, NodeAttributeRequestDto nodeAttributeDto);
    void delete(Long id);
}
