package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.Role;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceInUseException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.security.SecurityUtils;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import org.springframework.security.access.AccessDeniedException;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.RoleRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.RoleService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RoleServiceImpl implements RoleService {

    private static final String PROCESS_NOT_FOUND_MESSAGE = "Proceso no encontrado con ID: ";
    private static final String ROLE_NOT_FOUND_MESSAGE = "Rol no encontrado con ID: ";
    private static final String COMPANY_NOT_FOUND_MESSAGE = "Empresa no encontrada con ID: ";

    private final RoleRepository roleRepository;
    private final CompanyRepository companyRepository;
    private final ProcessRepository processRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(
            RoleRepository roleRepository,
            CompanyRepository companyRepository,
            ProcessRepository processRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.roleRepository = roleRepository;
        this.companyRepository = companyRepository;
        this.processRepository = processRepository;
        this.userRepository = userRepository;
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
    public List<RoleResponseDto> findByCompanyId(Long companyId, Long processId) {
        Company company = findCompany(companyId);
        if (processId != null) {
            Process process = findProcess(processId);
            validateProcessBelongsToCompany(process, company.getId());
            return roleRepository.findByCompanyIdAndProcesoId(companyId, processId).stream()
                    .map(this::toDto)
                    .toList();
        }

        return roleRepository.findByCompanyId(companyId).stream()
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
        Role role = findRole(id);
        Long tenantId = SecurityUtils.getCurrentCompanyId();
        if (role.getCompany() != null && !role.getCompany().getId().equals(tenantId)) {
            throw new AccessDeniedException("No tiene acceso a este rol");
        }
        return toDto(role);
    }

    @Override
    @Transactional
    public RoleResponseDto create(RoleRequestDto roleDto) {
        Company company = findCompany(roleDto.getCompanyId());
        Process process = resolveProcessForCompany(roleDto.getProcessId(), company.getId());
        validateRoleNameAvailable(company.getId(), roleDto.getProcessId(), roleDto.getNombre());

        Role role = new Role();
        role.setNombre(roleDto.getNombre());
        role.setDescripcion(roleDto.getDescripcion());
        role.setCompany(company);
        role.setProceso(process);

        return toDto(roleRepository.save(role));
    }

    @Override
    @Transactional
    public RoleResponseDto update(Long id, RoleRequestDto roleDto) {
        Role existingRole = findRole(id);
        Long tenantId = SecurityUtils.getCurrentCompanyId();
        if (existingRole.getCompany() != null && !existingRole.getCompany().getId().equals(tenantId)) {
            throw new AccessDeniedException("No tiene acceso a este rol");
        }
        Company company = findCompany(roleDto.getCompanyId());
        Process process = resolveProcessForCompany(roleDto.getProcessId(), company.getId());
        if (isRoleScopeOrNameChanged(existingRole, roleDto)) {
            validateRoleNameAvailable(company.getId(), roleDto.getProcessId(), roleDto.getNombre());
        }

        existingRole.setNombre(roleDto.getNombre());
        existingRole.setDescripcion(roleDto.getDescripcion());
        existingRole.setCompany(company);
        existingRole.setProceso(process);

        return toDto(roleRepository.save(existingRole));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Role role = findRole(id);
        Long tenantId = SecurityUtils.getCurrentCompanyId();
        if (role.getCompany() != null && !role.getCompany().getId().equals(tenantId)) {
            throw new AccessDeniedException("No tiene acceso a este rol");
        }
        if (userRepository.existsByRolId(id)) {
            throw new ResourceInUseException("No se puede eliminar el rol porque tiene usuarios asociados");
        }
        roleRepository.delete(role);
    }

    private RoleResponseDto toDto(Role role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setId(role.getId());
        dto.setNombre(role.getNombre());
        dto.setDescripcion(role.getDescripcion());
        dto.setCompanyId(role.getCompany() == null ? null : role.getCompany().getId());
        dto.setProcessId(role.getProceso() == null ? null : role.getProceso().getId());
        return dto;
    }

    private Process findProcess(Long processId) {
        return processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException(PROCESS_NOT_FOUND_MESSAGE + processId));
    }

    private Company findCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_MESSAGE + companyId));
    }

    private Role findRole(Long roleId) {
        return roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(ROLE_NOT_FOUND_MESSAGE + roleId));
    }

    private Process resolveProcessForCompany(Long processId, Long companyId) {
        if (processId == null) {
            return null;
        }

        Process process = findProcess(processId);
        validateProcessBelongsToCompany(process, companyId);
        return process;
    }

    private void validateProcessBelongsToCompany(Process process, Long companyId) {
        if (!process.getCompany().getId().equals(companyId)) {
            throw new BusinessRuleException("El proceso no pertenece a la empresa indicada");
        }
    }

    private boolean isRoleScopeOrNameChanged(Role existingRole, RoleRequestDto roleDto) {
        Long existingCompanyId = existingRole.getCompany() == null ? null : existingRole.getCompany().getId();
        Long existingProcessId = existingRole.getProceso() == null ? null : existingRole.getProceso().getId();
        return !roleDto.getCompanyId().equals(existingCompanyId)
                || !java.util.Objects.equals(roleDto.getProcessId(), existingProcessId)
                || !roleDto.getNombre().equals(existingRole.getNombre());
    }

    private void validateRoleNameAvailable(Long companyId, Long processId, String name) {
        boolean duplicated = processId == null
                ? roleRepository.existsByCompanyIdAndProcesoIsNullAndNombre(companyId, name)
                : roleRepository.existsByCompanyIdAndProcesoIdAndNombre(companyId, processId, name);
        if (duplicated) {
            throw new DuplicateResourceException("Ya existe un rol con nombre: " + name);
        }
    }
}
