package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;
import java.util.List;

public interface NodeService {
    List<NodeResponseDto> findAll();
    List<NodeResponseDto> findByVersionId(Long versionId);
    NodeResponseDto findById(Long id);
    NodeResponseDto create(NodeRequestDto nodeDto);
    NodeResponseDto update(Long id, NodeRequestDto nodeDto);
    void delete(Long id);
}
