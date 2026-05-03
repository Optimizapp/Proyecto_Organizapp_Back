package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.ProcessVersionRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessVersionResponseDto;
import java.util.List;

public interface ProcessVersionService {
    List<ProcessVersionResponseDto> findAll();
    List<ProcessVersionResponseDto> findByProcessId(Long processId);
    ProcessVersionResponseDto findById(Long id);
    ProcessVersionResponseDto create(ProcessVersionRequestDto processVersionDto);
    ProcessVersionResponseDto update(Long id, ProcessVersionRequestDto processVersionDto);
    ProcessVersionResponseDto publish(Long id);
    void delete(Long id);
}
