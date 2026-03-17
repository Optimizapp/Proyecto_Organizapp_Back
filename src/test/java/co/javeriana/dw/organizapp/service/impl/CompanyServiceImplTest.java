package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.CompanyRequestDto;
import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.exception.CompanyNotFoundException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Spy // Usamos Spy en lugar de Mock para que ModelMapper funcione de verdad
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private CompanyServiceImpl companyService;

    private Company company;
    private CompanyRequestDto requestDto;

    @BeforeEach
    void setUp() {
        company = new Company();
        company.setId(1L);
        company.setName("Test Corp");
        company.setNit("123456");
        company.setIndustry("Tech");

        requestDto = new CompanyRequestDto();
        requestDto.setName("New Name");
        requestDto.setNit("654321");
        requestDto.setIndustry("Finance");
    }

    @Test
    void findAllShouldReturnDtoList() {
        when(companyRepository.findAll()).thenReturn(List.of(company));

        List<CompanyResponseDto> result = companyService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Corp", result.get(0).getName());
    }

    @Test
    void findByIdShouldReturnDto() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        CompanyResponseDto result = companyService.findById(1L);

        assertNotNull(result);
        assertEquals("Test Corp", result.getName());
    }

    @Test
    void createShouldReturnResponseDto() {
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponseDto result = companyService.create(requestDto);

        assertNotNull(result);
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void updateShouldModifyExistingCompanyFromDto() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        CompanyResponseDto result = companyService.update(1L, requestDto);

        assertNotNull(result);
        // Verificamos que los datos del DTO se pasaron a la entidad guardada
        assertEquals(requestDto.getName(), result.getName());
        verify(companyRepository).save(company);
    }

    @Test
    void deleteShouldCallRepository() {
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        doNothing().when(companyRepository).delete(company);

        companyService.delete(1L);

        verify(companyRepository).delete(company);
    }

    @Test
    void findByIdShouldThrowExceptionWhenNotFound() {
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CompanyNotFoundException.class, () -> companyService.findById(99L));
    }
}