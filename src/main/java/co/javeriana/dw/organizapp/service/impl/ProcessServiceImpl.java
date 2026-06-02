package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CreateProcessRequest;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.dto.UpdateProcessRequest;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Pool;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.ProcessStatus;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.security.SecurityUtils;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import org.springframework.security.access.AccessDeniedException;
import co.javeriana.dw.organizapp.repository.PoolRepository;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.ProcessService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessServiceImpl implements ProcessService {

    private static final String PROCESS_NOT_FOUND_MESSAGE = "Proceso no encontrado con ID: ";
    private static final String COMPANY_NOT_FOUND_MESSAGE = "Empresa no encontrada con ID: ";
    private static final String USER_NOT_FOUND_MESSAGE = "Usuario no encontrado con ID: ";

    private final ProcessRepository processRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final PoolRepository poolRepository;
    private final ModelMapper modelMapper;

    public ProcessServiceImpl(
            ProcessRepository processRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository,
            PoolRepository poolRepository,
            ModelMapper modelMapper) {
        this.processRepository = processRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.poolRepository = poolRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessResponseDto> findAll(Long companyId, String status) {
        ProcessStatus parsedStatus = status == null ? null : parseStatus(status);
        List<Process> processes;
        if (companyId != null && parsedStatus != null) {
            processes = processRepository.findByCompanyIdAndStatus(companyId, parsedStatus);
        } else if (companyId != null) {
            processes = processRepository.findByCompanyId(companyId);
        } else if (parsedStatus != null) {
            processes = processRepository.findByStatus(parsedStatus);
        } else {
            processes = processRepository.findAll();
        }
        return processes.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessResponseDto findById(Long id) {
        Process process = findExistingProcess(id);
        Long tenantId = SecurityUtils.getCurrentCompanyId();
        if (!process.getCompany().getId().equals(tenantId)) {
            throw new AccessDeniedException("No tiene acceso a este proceso");
        }
        return convertToDto(process);
    }

    @Override
    @Transactional
    public ProcessResponseDto create(CreateProcessRequest processDto) {
        Company company = findCompany(processDto.getCompanyId());
        User user = findUser(processDto.getUserId());
        validateUserBelongsToCompany(user, company);
        Pool mainPool = resolveMainPool(processDto.getMainPoolId(), company.getId());
        validateProcessNameAvailable(company.getId(), processDto.getName());

        Process process = new Process();
        process.setName(processDto.getName());
        process.setDescription(processDto.getDescription());
        process.setCategory(processDto.getCategory());
        process.setCompany(company);
        process.setUser(user);
        process.setMainPool(mainPool);
        process.setStatus(parseStatus(processDto.getStatus()));

        return convertToDto(processRepository.save(process));
    }

    @Override
    @Transactional
    public ProcessResponseDto update(Long id, UpdateProcessRequest processDto) {
        Process existingProcess = findExistingProcess(id);
        Long tenantId = SecurityUtils.getCurrentCompanyId();
        if (!existingProcess.getCompany().getId().equals(tenantId)) {
            throw new AccessDeniedException("No tiene acceso a este proceso");
        }
        Company company = findCompany(processDto.getCompanyId());
        User user = findUser(processDto.getUserId());
        validateUserBelongsToCompany(user, company);
        Pool mainPool = resolveMainPool(processDto.getMainPoolId(), company.getId());
        if (!existingProcess.getCompany().getId().equals(company.getId())
                || !existingProcess.getName().equals(processDto.getName())) {
            validateProcessNameAvailable(company.getId(), processDto.getName());
        }

        existingProcess.setName(processDto.getName());
        existingProcess.setDescription(processDto.getDescription());
        existingProcess.setCategory(processDto.getCategory());
        existingProcess.setStatus(parseStatus(processDto.getStatus()));
        existingProcess.setCompany(company);
        existingProcess.setUser(user);
        existingProcess.setMainPool(mainPool);

        return convertToDto(processRepository.save(existingProcess));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Process process = findExistingProcess(id);
        Long tenantId = SecurityUtils.getCurrentCompanyId();
        if (!process.getCompany().getId().equals(tenantId)) {
            throw new AccessDeniedException("No tiene acceso a este proceso");
        }
        if (process.getStatus() != ProcessStatus.INACTIVE) {
            process.setStatus(ProcessStatus.INACTIVE);
            processRepository.save(process);
        }
    }

    private ProcessResponseDto convertToDto(Process process) {
        ProcessResponseDto dto = modelMapper.map(process, ProcessResponseDto.class);
        dto.setCompanyId(process.getCompany().getId());
        dto.setUserId(process.getUser().getId());
        dto.setMainPoolId(process.getMainPool() == null ? null : process.getMainPool().getId());
        dto.setStatus(process.getStatus().name());
        return dto;
    }

    private ProcessStatus parseStatus(String statusStr) {
        try {
            return ProcessStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new BusinessRuleException("Estado de proceso invalido: " + statusStr);
        }
    }

    private void validateProcessNameAvailable(Long companyId, String name) {
        if (processRepository.existsByCompanyIdAndName(companyId, name)) {
            throw new DuplicateResourceException("Ya existe un proceso con nombre: " + name);
        }
    }

    private Process findExistingProcess(Long id) {
        return processRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROCESS_NOT_FOUND_MESSAGE + id));
    }

    private Company findCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(COMPANY_NOT_FOUND_MESSAGE + companyId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));
    }

    private Pool resolveMainPool(Long mainPoolId, Long companyId) {
        if (mainPoolId != null) {
            Pool pool = poolRepository.findById(mainPoolId)
                    .orElseThrow(() -> new ResourceNotFoundException("Pool no encontrado con ID: " + mainPoolId));
            validatePoolBelongsToCompany(pool, companyId);
            return pool;
        }

        return poolRepository.findFirstByCompanyIdAndActiveTrueOrderByIdAsc(companyId)
                .orElse(null);
    }

    private void validatePoolBelongsToCompany(Pool pool, Long companyId) {
        if (!pool.getCompany().getId().equals(companyId)) {
            throw new BusinessRuleException("El pool principal no pertenece a la empresa indicada");
        }
    }

    private void validateUserBelongsToCompany(User user, Company company) {
        if (!user.getCompany().getId().equals(company.getId())) {
            throw new BusinessRuleException("El usuario responsable no pertenece a la empresa indicada");
        }
    }
}
