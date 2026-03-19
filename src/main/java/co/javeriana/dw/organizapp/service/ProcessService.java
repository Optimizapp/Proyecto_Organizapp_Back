package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.ProcessRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import java.util.List;

public interface ProcessService {
    List<ProcessResponseDto> findAll();
    ProcessResponseDto findById(Long id);
    ProcessResponseDto create(ProcessRequestDto processDto);
    ProcessResponseDto update(Long id, ProcessRequestDto processDto);
    void delete(Long id);
}