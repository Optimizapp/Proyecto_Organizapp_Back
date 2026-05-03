package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CreateProcessRequest;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.dto.UpdateProcessRequest;
import java.util.List;

public interface ProcessService {
    List<ProcessResponseDto> findAll(Long companyId, String status);
    ProcessResponseDto findById(Long id);
    ProcessResponseDto create(CreateProcessRequest processDto);
    ProcessResponseDto update(Long id, UpdateProcessRequest processDto);
    void delete(Long id);
}
