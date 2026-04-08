package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.CompanyRequestDto;
import co.javeriana.dw.organizapp.dto.CompanyResponseDto;

import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class CompanyServiceTest {

    private final CompanyService companyService = mock(CompanyService.class);

    public CompanyServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findAll() {
        when(companyService.findAll()).thenReturn(List.of(new CompanyResponseDto()));

        List<CompanyResponseDto> result = companyService.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void findById() {
        when(companyService.findById(1L)).thenReturn(new CompanyResponseDto());

        CompanyResponseDto result = companyService.findById(1L);

        assertNotNull(result);
    }

    @Test
    void create() {
        CompanyRequestDto request = new CompanyRequestDto();
        CompanyResponseDto response = new CompanyResponseDto();

        when(companyService.create(any())).thenReturn(response);

        CompanyResponseDto result = companyService.create(request);

        assertNotNull(result);
    }

    @Test
    void update() {
        CompanyRequestDto request = new CompanyRequestDto();
        CompanyResponseDto response = new CompanyResponseDto();

        when(companyService.update(eq(1L), any())).thenReturn(response);

        CompanyResponseDto result = companyService.update(1L, request);

        assertNotNull(result);
    }

    @Test
    void delete() {
        doNothing().when(companyService).delete(1L);

        assertDoesNotThrow(() -> companyService.delete(1L));
    }
}