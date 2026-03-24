package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.ProcessRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.entity.ProcessStatus;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.service.ProcessService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProcessServiceImpl implements ProcessService {

    private final ProcessRepository processRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public ProcessServiceImpl(ProcessRepository processRepository, 
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
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProcessResponseDto findById(Long id) {
        Process process = processRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado con ID: " + id));
        return convertToDto(process);
    }

    @Override
    @Transactional
    public ProcessResponseDto create(ProcessRequestDto processDto) {
        // Validamos existencia de dependencias
        Company company = companyRepository.findById(processDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
        User user = userRepository.findById(processDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado"));

        Process process = modelMapper.map(processDto, Process.class);
        
        // Seteamos relaciones y estado manualmente para asegurar integridad
        process.setCompany(company);
        process.setUser(user);
        process.setStatus(parseStatus(processDto.getStatus()));

        return convertToDto(processRepository.save(process));
    }

    @Override
    @Transactional
    public ProcessResponseDto update(Long id, ProcessRequestDto processDto) {
        Process existingProcess = processRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proceso no encontrado"));

        Company company = companyRepository.findById(processDto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada"));
        User user = userRepository.findById(processDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        // Actualización manual de campos para evitar sobreescribir createdAt
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
        if (!processRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Proceso no encontrado");
        }
        processRepository.deleteById(id);
    }

    // --- Métodos Privados de Apoyo ---

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
        } catch (IllegalArgumentException | NullPointerException e) {
            // Si el estado enviado no existe en el Enum, usamos el valor por defecto
            return ProcessStatus.DRAFT;
        }
    }
}