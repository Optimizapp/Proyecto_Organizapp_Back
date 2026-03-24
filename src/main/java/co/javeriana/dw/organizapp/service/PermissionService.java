package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.PermissionRequestDto;
import co.javeriana.dw.organizapp.dto.PermissionResponseDto;
import java.util.List;

public interface PermissionService {
    List<PermissionResponseDto> findAll();
    List<PermissionResponseDto> findByRoleId(Long roleId);
    PermissionResponseDto findById(Long id);
    PermissionResponseDto create(PermissionRequestDto permissionDto);
    PermissionResponseDto update(Long id, PermissionRequestDto permissionDto);
    void delete(Long id);
}
