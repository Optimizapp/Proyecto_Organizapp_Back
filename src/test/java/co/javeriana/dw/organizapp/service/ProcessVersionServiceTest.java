package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.ProcessVersionRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessVersionResponseDto;

import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ProcessVersionServiceTest {

    private final ProcessVersionService processVersionService = mock(ProcessVersionService.class);

    public ProcessVersionServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        when(processVersionService.findAll()).thenReturn(List.of(new ProcessVersionResponseDto()));

        List<ProcessVersionResponseDto> result = processVersionService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findByProcessId() {
        when(processVersionService.findByProcessId(1L)).thenReturn(List.of(new ProcessVersionResponseDto()));

        List<ProcessVersionResponseDto> result = processVersionService.findByProcessId(1L);

        assertNotNull(result);
    }

    @Test
    void findById() {
        when(processVersionService.findById(1L)).thenReturn(new ProcessVersionResponseDto());

        ProcessVersionResponseDto result = processVersionService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void create() {
        ProcessVersionRequestDto request = new ProcessVersionRequestDto();
        ProcessVersionResponseDto response = new ProcessVersionResponseDto();

        when(processVersionService.create(any())).thenReturn(response);

        ProcessVersionResponseDto result = processVersionService.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        ProcessVersionRequestDto request = new ProcessVersionRequestDto();
        ProcessVersionResponseDto response = new ProcessVersionResponseDto();

        when(processVersionService.update(eq(1L), any())).thenReturn(response);

        ProcessVersionResponseDto result = processVersionService.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        doNothing().when(processVersionService).delete(1L);

        assertDoesNotThrow(() -> processVersionService.delete(1L));
    }
}