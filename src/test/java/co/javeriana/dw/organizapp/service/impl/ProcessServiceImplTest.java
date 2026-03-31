package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.ProcessRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.entity.Process;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import co.javeriana.dw.organizapp.repository.ProcessRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessServiceImplTest {

    @Mock
    private ProcessRepository processRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ProcessServiceImpl processService;

    @Test
    void testFindAll() {
        Process process = new Process();
        process.setId(1L);
        process.setName("Test Process");

        ProcessResponseDto dto = new ProcessResponseDto();
        dto.setId(1L);
        dto.setName("Test Process");

        when(processRepository.findAll()).thenReturn(Arrays.asList(process));
        when(modelMapper.map(any(Process.class), eq(ProcessResponseDto.class))).thenReturn(dto);

        List<ProcessResponseDto> result = processService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Process", result.get(0).getName());
    }

    @Test
    void testFindByIdSuccess() {
        Process process = new Process();
        process.setId(1L);
        process.setName("Test Process");

        ProcessResponseDto dto = new ProcessResponseDto();
        dto.setId(1L);
        dto.setName("Test Process");

        when(processRepository.findById(1L)).thenReturn(Optional.of(process));
        when(modelMapper.map(any(Process.class), eq(ProcessResponseDto.class))).thenReturn(dto);

        ProcessResponseDto result = processService.findById(1L);

        assertNotNull(result);
        assertEquals("Test Process", result.getName());
    }

    @Test
    void testFindByIdNotFound() {
        when(processRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> processService.findById(1L));
    }

    @Test
    void testCreate() {
        ProcessRequestDto requestDto = new ProcessRequestDto();
        requestDto.setName("Test Process");
        requestDto.setCompanyId(1L);

        Process process = new Process();
        process.setId(1L);
        process.setName("Test Process");

        Company company = new Company();
        company.setId(1L);
        company.setName("Test Company");

        ProcessResponseDto responseDto = new ProcessResponseDto();
        responseDto.setId(1L);
        responseDto.setName("Test Process");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(modelMapper.map(any(ProcessRequestDto.class), eq(Process.class))).thenReturn(process);
        when(processRepository.save(any(Process.class))).thenReturn(process);
        when(modelMapper.map(any(Process.class), eq(ProcessResponseDto.class))).thenReturn(responseDto);

        ProcessResponseDto result = processService.create(requestDto);

        assertNotNull(result);
        assertEquals("Test Process", result.getName());
    }

    @Test
    void testDelete() {
        Process process = new Process();
        process.setId(1L);

        when(processRepository.findById(1L)).thenReturn(Optional.of(process));
        doNothing().when(processRepository).delete(any(Process.class));

        assertDoesNotThrow(() -> processService.delete(1L));
        verify(processRepository, times(1)).delete(any(Process.class));
    }
}
