package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CompanyRequestDto;
import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.exception.CompanyNotFoundException;
import co.javeriana.dw.organizapp.exception.DuplicateCompanyException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
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
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company company;
    private CompanyRequestDto companyRequestDto;
    private CompanyResponseDto companyResponseDto;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);
        company.setName("Test Company");
        company.setNit("123456789");

        companyRequestDto = new CompanyRequestDto();
        companyRequestDto.setName("Test Company");
        companyRequestDto.setNit("123456789");

        companyResponseDto = new CompanyResponseDto();
        companyResponseDto.setId(1L);
        companyResponseDto.setName("Test Company");
        companyResponseDto.setNit("123456789");
    }

    @Test
    void findAll_ShouldReturnListOfCompanies() {
        when(companyRepository.findAll()).thenReturn(Arrays.asList(company));
        when(modelMapper.map(any(Company.class), eq(CompanyResponseDto.class))).thenReturn(companyResponseDto);

        List<CompanyResponseDto> result = companyService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Company", result.get(0).getName());
        verify(companyRepository).findAll();
    }

    @Test
    void findById_WhenCompanyExists_ShouldReturnCompany() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(modelMapper.map(company, CompanyResponseDto.class)).thenReturn(companyResponseDto);

        CompanyResponseDto result = companyService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Company", result.getName());
        verify(companyRepository).findById(1L);
    }

    @Test
    void findById_WhenCompanyNotExists_ShouldThrowException() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> companyService.findById(1L));
        verify(companyRepository).findById(1L);
    }

    @Test
    void create_WhenValidCompany_ShouldReturnCreatedCompany() {
        when(companyRepository.existsByName(anyString())).thenReturn(false);
        when(companyRepository.existsByNit(anyString())).thenReturn(false);
        when(modelMapper.map(companyRequestDto, Company.class)).thenReturn(company);
        when(companyRepository.save(company)).thenReturn(company);
        when(modelMapper.map(company, CompanyResponseDto.class)).thenReturn(companyResponseDto);

        CompanyResponseDto result = companyService.create(companyRequestDto);

        assertNotNull(result);
        assertEquals("Test Company", result.getName());
        verify(companyRepository).save(company);
    }

    @Test
    void create_WhenDuplicateName_ShouldThrowException() {
        when(companyRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(DuplicateCompanyException.class, () -> companyService.create(companyRequestDto));
        verify(companyRepository, never()).save(any());
    }

    @Test
    void delete_WhenCompanyExists_ShouldDeleteCompany() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        assertDoesNotThrow(() -> companyService.delete(1L));
        verify(companyRepository).delete(company);
    }

    @Test
    void delete_WhenCompanyNotExists_ShouldThrowException() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> companyService.delete(1L));
        verify(companyRepository, never()).delete(any());
    }
}
