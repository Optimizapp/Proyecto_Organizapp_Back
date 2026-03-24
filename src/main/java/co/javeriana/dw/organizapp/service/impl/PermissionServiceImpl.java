package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.PermissionRequestDto;
import co.javeriana.dw.organizapp.dto.PermissionResponseDto;
import co.javeriana.dw.organizapp.entity.Permission;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.PermissionRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.service.PermissionService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    public PermissionServiceImpl(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            ModelMapper modelMapper) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDto> findAll() {
        return permissionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseDto> findByRoleId(Long roleId) {
        roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + roleId));
        return permissionRepository.findByRolId(roleId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponseDto findById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
        return toDto(permission);
    }

    @Override
    @Transactional
    public PermissionResponseDto create(PermissionRequestDto permissionDto) {
        Role role = roleRepository.findById(permissionDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + permissionDto.getRoleId()));

        Permission permission = modelMapper.map(permissionDto, Permission.class);
        permission.setRol(role);

        return toDto(permissionRepository.save(permission));
    }

    @Override
    @Transactional
    public PermissionResponseDto update(Long id, PermissionRequestDto permissionDto) {
        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
        Role role = roleRepository.findById(permissionDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + permissionDto.getRoleId()));

        existingPermission.setCodigo(permissionDto.getCodigo());
        existingPermission.setDescripcion(permissionDto.getDescripcion());
        existingPermission.setRol(role);

        return toDto(permissionRepository.save(existingPermission));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
        permissionRepository.delete(permission);
    }

    private PermissionResponseDto toDto(Permission permission) {
        PermissionResponseDto dto = modelMapper.map(permission, PermissionResponseDto.class);
        dto.setRoleId(permission.getRol().getId());
        return dto;
    }
}
