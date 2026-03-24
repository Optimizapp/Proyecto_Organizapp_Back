package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.FlowRequestDto;
import co.javeriana.dw.organizapp.dto.FlowResponseDto;
import java.util.List;

public interface FlowService {
    List<FlowResponseDto> findAll();
    List<FlowResponseDto> findByVersionId(Long versionId);
    FlowResponseDto findById(Long id);
    FlowResponseDto create(FlowRequestDto flowDto);
    FlowResponseDto update(Long id, FlowRequestDto flowDto);
    void delete(Long id);
}
