package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.ProcessRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.ProcessStatus;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.InvalidRequestException;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
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
    private final ModelMapper modelMapper;

    public ProcessServiceImpl(
            ProcessRepository processRepository,
            CompanyRepository companyRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.processRepository = processRepository;
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessResponseDto> findAll() {
        return processRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessResponseDto findById(Long id) {
        return convertToDto(findExistingProcess(id));
    }

    @Override
    @Transactional
    public ProcessResponseDto create(ProcessRequestDto processDto) {
        Company company = findCompany(processDto.getCompanyId());
        User user = findUser(processDto.getUserId());

        Process process = modelMapper.map(processDto, Process.class);
        process.setCompany(company);
        process.setUser(user);
        process.setStatus(parseStatus(processDto.getStatus()));

        return convertToDto(processRepository.save(process));
    }

    @Override
    @Transactional
    public ProcessResponseDto update(Long id, ProcessRequestDto processDto) {
        Process existingProcess = findExistingProcess(id);
        Company company = findCompany(processDto.getCompanyId());
        User user = findUser(processDto.getUserId());

        existingProcess.setName(processDto.getName());
        existingProcess.setDescription(processDto.getDescription());
        existingProcess.setStatus(parseStatus(processDto.getStatus()));
        existingProcess.setCompany(company);
        existingProcess.setUser(user);

        return convertToDto(processRepository.save(existingProcess));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Process process = findExistingProcess(id);
        if (process.getStatus() != ProcessStatus.INACTIVE) {
            process.setStatus(ProcessStatus.INACTIVE);
            processRepository.save(process);
        }
    }

    private ProcessResponseDto convertToDto(Process process) {
        ProcessResponseDto dto = modelMapper.map(process, ProcessResponseDto.class);
        dto.setCompanyId(process.getCompany().getId());
        dto.setUserId(process.getUser().getId());
        dto.setStatus(process.getStatus().name());
        return dto;
    }

    private ProcessStatus parseStatus(String statusStr) {
        try {
            return ProcessStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new InvalidRequestException("Estado de proceso invalido: " + statusStr);
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
}
