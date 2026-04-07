package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.ProcessVersionRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessVersionResponseDto;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.ProcessVersion;
import co.javeriana.dw.organizapp.entity.ProcessVersionStatus;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.ProcessVersionRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.ProcessVersionService;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessVersionServiceImpl implements ProcessVersionService {

    private static final String PROCESS_VERSION_NOT_FOUND_MESSAGE = "Version de proceso no encontrada con ID: ";

    private final ProcessVersionRepository processVersionRepository;
    private final ProcessRepository processRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ProcessVersionServiceImpl(
            ProcessVersionRepository processVersionRepository,
            ProcessRepository processRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.processVersionRepository = processVersionRepository;
        this.processRepository = processRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessVersionResponseDto> findAll() {
        return processVersionRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProcessVersionResponseDto> findByProcessId(Long processId) {
        processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con ID: " + processId));
        return processVersionRepository.findByProcesoId(processId).stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessVersionResponseDto findById(Long id) {
        ProcessVersion processVersion = processVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROCESS_VERSION_NOT_FOUND_MESSAGE + id));
        return toDto(processVersion);
    }

    @Override
    @Transactional
    public ProcessVersionResponseDto create(ProcessVersionRequestDto processVersionDto) {
        Process process = findProcess(processVersionDto.getProcessId());
        User createdBy = findUser(processVersionDto.getCreatedByUserId());

        ProcessVersion processVersion = modelMapper.map(processVersionDto, ProcessVersion.class);
        processVersion.setProceso(process);
        processVersion.setCreadoPor(createdBy);
        processVersion.setEstado(parseStatus(processVersionDto.getEstado()));

        return toDto(processVersionRepository.save(processVersion));
    }

    @Override
    @Transactional
    public ProcessVersionResponseDto update(Long id, ProcessVersionRequestDto processVersionDto) {
        ProcessVersion existingProcessVersion = processVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROCESS_VERSION_NOT_FOUND_MESSAGE + id));
        Process process = findProcess(processVersionDto.getProcessId());
        User createdBy = findUser(processVersionDto.getCreatedByUserId());

        existingProcessVersion.setProceso(process);
        existingProcessVersion.setNumeroVersion(processVersionDto.getNumeroVersion());
        existingProcessVersion.setCreadoPor(createdBy);
        existingProcessVersion.setEstado(parseStatus(processVersionDto.getEstado()));

        return toDto(processVersionRepository.save(existingProcessVersion));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProcessVersion processVersion = processVersionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PROCESS_VERSION_NOT_FOUND_MESSAGE + id));
        processVersionRepository.delete(processVersion);
    }

    private ProcessVersionResponseDto toDto(ProcessVersion processVersion) {
        ProcessVersionResponseDto dto = modelMapper.map(processVersion, ProcessVersionResponseDto.class);
        dto.setProcessId(processVersion.getProceso().getId());
        dto.setCreatedByUserId(processVersion.getCreadoPor().getId());
        dto.setEstado(processVersion.getEstado().name());
        return dto;
    }

    private Process findProcess(Long processId) {
        return processRepository.findById(processId)
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con ID: " + processId));
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + userId));
    }

    private ProcessVersionStatus parseStatus(String status) {
        try {
            return ProcessVersionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException ex) {
            return ProcessVersionStatus.BORRADOR;
        }
    }
}
