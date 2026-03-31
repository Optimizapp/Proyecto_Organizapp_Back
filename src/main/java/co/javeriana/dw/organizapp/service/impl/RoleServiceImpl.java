package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.ResourceInUseException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.service.RoleService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private static final String PROCESS_NOT_FOUND_MESSAGE = "Proceso no encontrado con ID: ";
    private static final String ROLE_NOT_FOUND_MESSAGE = "Rol no encontrado con ID: ";

    private final RoleRepository roleRepository;
    private final ProcessRepository processRepository;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepository roleRepository, ProcessRepository processRepository, ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.processRepository = processRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> findAll() {
        return roleRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> findByProcessId(Long processId) {
        findProcess(processId);
        return roleRepository.findByProcesoId(processId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto findById(Long id) {
        return toDto(findRole(id));
    }

    @Override
    @Transactional
    public RoleResponseDto create(RoleRequestDto roleDto) {
        Process process = findProcess(roleDto.getProcessId());

        Role role = modelMapper.map(roleDto, Role.class);
        role.setProceso(process);

        return toDto(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponseDto update(Long id, RoleRequestDto roleDto) {
        Role existingRole = findRole(id);
        Process process = findProcess(roleDto.getProcessId());

        existingRole.setNombre(roleDto.getNombre());
        existingRole.setDescripcion(roleDto.getDescripcion());
        existingRole.setProceso(process);

        return toDto(roleRepository.save(existingRole));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = findRole(id);
        if (!role.getUsuarios().isEmpty()) {
            throw new ResourceInUseException("No se puede eliminar el rol porque tiene usuarios asociados");
        }
        roleRepository.delete(role);
    }

    private RoleResponseDto toDto(Role role) {
        RoleResponseDto dto = modelMapper.map(role, RoleResponseDto.class);
        dto.setProcessId(role.getProceso().getId());
        return dto;
    }

    private Process findProcess(Long processId) {
        return processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException(PROCESS_NOT_FOUND_MESSAGE + processId));
    }

    private Role findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_MESSAGE + roleId));
    }
}
