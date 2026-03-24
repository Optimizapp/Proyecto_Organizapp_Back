package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import java.util.List;

public interface RoleService {
    List<RoleResponseDto> findAll();
    List<RoleResponseDto> findByProcessId(Long processId);
    RoleResponseDto findById(Long id);
    RoleResponseDto create(RoleRequestDto roleDto);
    RoleResponseDto update(Long id, RoleRequestDto roleDto);
    void delete(Long id);
}
