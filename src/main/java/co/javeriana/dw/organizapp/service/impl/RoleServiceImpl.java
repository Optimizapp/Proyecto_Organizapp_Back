package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.service.RoleService;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

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
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> findByProcessId(Long processId) {
        processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con ID: " + processId));
        return roleRepository.findByProcesoId(processId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto findById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        return toDto(role);
    }

    @Override
    @Transactional
    public RoleResponseDto create(RoleRequestDto roleDto) {
        Process process = processRepository.findById(roleDto.getProcessId())
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con ID: " + roleDto.getProcessId()));

        Role role = modelMapper.map(roleDto, Role.class);
        role.setProceso(process);

        return toDto(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponseDto update(Long id, RoleRequestDto roleDto) {
        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        Process process = processRepository.findById(roleDto.getProcessId())
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con ID: " + roleDto.getProcessId()));

        existingRole.setNombre(roleDto.getNombre());
        existingRole.setDescripcion(roleDto.getDescripcion());
        existingRole.setProceso(process);

        return toDto(roleRepository.save(existingRole));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));
        roleRepository.delete(role);
    }

    private RoleResponseDto toDto(Role role) {
        RoleResponseDto dto = modelMapper.map(role, RoleResponseDto.class);
        dto.setProcessId(role.getProceso().getId());
        return dto;
    }
}
