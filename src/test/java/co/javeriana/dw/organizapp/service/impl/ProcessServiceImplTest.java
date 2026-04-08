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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessServiceImplTest {

    @Mock
    private ProcessRepository processRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProcessServiceImpl processService;

private Process buildProcess() {
    Company company = new Company();
    company.setId(1L);

    User user = new User();
    user.setId(1L);

    Process process = new Process();
    process.setId(1L);
    process.setName("Test Process");
    process.setCompany(company);
    process.setUser(user);
    process.setStatus(ProcessStatus.ACTIVE); 

    return process;
}

    private ProcessResponseDto buildResponseDto() {
        ProcessResponseDto dto = new ProcessResponseDto();
        dto.setId(1L);
        dto.setName("Test Process");
        dto.setCompanyId(1L);
        dto.setUserId(1L);
        return dto;
    }

    @Test
    void testFindAll() {
        Process process = buildProcess();

        when(processRepository.findAll()).thenReturn(List.of(process));
        when(modelMapper.map(any(Process.class), eq(ProcessResponseDto.class)))
                .thenReturn(buildResponseDto());

        List<ProcessResponseDto> result = processService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Process", result.get(0).getName());

        verify(processRepository).findAll();
    }

    @Test
    void testFindByIdSuccess() {
        Process process = buildProcess();

        when(processRepository.findById(1L)).thenReturn(Optional.of(process));
        when(modelMapper.map(any(Process.class), eq(ProcessResponseDto.class)))
                .thenReturn(buildResponseDto());

        ProcessResponseDto result = processService.findById(1L);

        assertNotNull(result);
        assertEquals("Test Process", result.getName());
    }

    @Test
    void testFindByIdNotFound() {
        when(processRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> processService.findById(1L));
    }

    @Test
    void testCreate() {
        ProcessRequestDto requestDto = new ProcessRequestDto();
        requestDto.setName("Test Process");
        requestDto.setCompanyId(1L);
        requestDto.setUserId(1L);
        requestDto.setStatus("ACTIVE"); // 👈 IMPORTANTE

        Company company = new Company();
        company.setId(1L);

        User user = new User();
        user.setId(1L);

        Process process = buildProcess();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(any(ProcessRequestDto.class), eq(Process.class)))
                .thenReturn(process);
        when(processRepository.save(any(Process.class)))
                .thenReturn(process);
        when(modelMapper.map(any(Process.class), eq(ProcessResponseDto.class)))
                .thenReturn(buildResponseDto());

        ProcessResponseDto result = processService.create(requestDto);

        assertNotNull(result);
        assertEquals("Test Process", result.getName());

        verify(processRepository).save(any(Process.class));
    }

    @Test
    void testDelete() {
        Process process = buildProcess();

        when(processRepository.findById(1L)).thenReturn(Optional.of(process));

        processService.delete(1L);

        // 👇 tu implementación hace soft delete
        verify(processRepository).save(process);
    }
}