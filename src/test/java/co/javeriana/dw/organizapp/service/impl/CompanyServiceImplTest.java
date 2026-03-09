package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.exception.CompanyNotFoundException;
import co.javeriana.dw.organizapp.exception.DuplicateCompanyException;
import co.javeriana.dw.organizapp.repository.CompanyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyServiceImpl companyService;

    @Test
    void findAllShouldReturnAllCompanies() {
        List<Company> companies = List.of(
                buildCompany(1L, "Alpha", "900100100", "Tech"),
                buildCompany(2L, "Beta", "900200200", "Finance")
        );

        when(companyRepository.findAll()).thenReturn(companies);

        List<Company> result = companyService.findAll();

        assertEquals(companies, result);
        verify(companyRepository).findAll();
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void findByIdShouldReturnCompanyWhenItExists() {
        Company company = buildCompany(1L, "Alpha", "900100100", "Tech");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        Company result = companyService.findById(1L);

        assertSame(company, result);
        verify(companyRepository).findById(1L);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void findByIdShouldThrowWhenCompanyDoesNotExist() {
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(CompanyNotFoundException.class, () -> companyService.findById(99L));

        assertEquals("Empresa no encontrada con ID: 99", exception.getMessage());
        verify(companyRepository).findById(99L);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void createShouldSaveCompany() {
        Company companyToCreate = buildCompany(null, "Gamma", "900300300", "Health");
        Company savedCompany = buildCompany(3L, "Gamma", "900300300", "Health");

        when(companyRepository.existsByName("Gamma")).thenReturn(false);
        when(companyRepository.existsByNit("900300300")).thenReturn(false);
        when(companyRepository.save(companyToCreate)).thenReturn(savedCompany);

        Company result = companyService.create(companyToCreate);

        assertSame(savedCompany, result);
        verify(companyRepository).existsByName("Gamma");
        verify(companyRepository).existsByNit("900300300");
        verify(companyRepository).save(companyToCreate);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void createShouldThrowWhenNameAlreadyExists() {
        Company companyToCreate = buildCompany(null, "Gamma", "900300300", "Health");
        when(companyRepository.existsByName("Gamma")).thenReturn(true);

        DuplicateCompanyException exception = assertThrows(DuplicateCompanyException.class,
                () -> companyService.create(companyToCreate));

        assertEquals("Ya existe una empresa con nombre: Gamma", exception.getMessage());
        verify(companyRepository).existsByName("Gamma");
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void createShouldThrowWhenNitAlreadyExists() {
        Company companyToCreate = buildCompany(null, "Gamma", "900300300", "Health");
        when(companyRepository.existsByName("Gamma")).thenReturn(false);
        when(companyRepository.existsByNit("900300300")).thenReturn(true);

        DuplicateCompanyException exception = assertThrows(DuplicateCompanyException.class,
                () -> companyService.create(companyToCreate));

        assertEquals("Ya existe una empresa con NIT: 900300300", exception.getMessage());
        verify(companyRepository).existsByName("Gamma");
        verify(companyRepository).existsByNit("900300300");
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void updateShouldModifyExistingCompanyAndSaveIt() {
        Company existingCompany = buildCompany(1L, "Alpha", "900100100", "Tech");
        Company updatedData = buildCompany(null, "Alpha Updated", "900100101", "Retail");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.existsByName("Alpha Updated")).thenReturn(false);
        when(companyRepository.existsByNit("900100101")).thenReturn(false);
        when(companyRepository.save(existingCompany)).thenReturn(existingCompany);

        Company result = companyService.update(1L, updatedData);

        assertSame(existingCompany, result);
        assertEquals("Alpha Updated", existingCompany.getName());
        assertEquals("900100101", existingCompany.getNit());
        assertEquals("Retail", existingCompany.getIndustry());
        verify(companyRepository).findById(1L);
        verify(companyRepository).existsByName("Alpha Updated");
        verify(companyRepository).existsByNit("900100101");
        verify(companyRepository).save(existingCompany);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void updateShouldThrowWhenCompanyDoesNotExist() {
        Company updatedData = buildCompany(null, "Alpha Updated", "900100101", "Retail");
        when(companyRepository.findById(77L)).thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(CompanyNotFoundException.class,
                () -> companyService.update(77L, updatedData));

        assertEquals("Empresa no encontrada con ID: 77", exception.getMessage());
        verify(companyRepository).findById(77L);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void updateShouldThrowWhenNameAlreadyExists() {
        Company existingCompany = buildCompany(1L, "Alpha", "900100100", "Tech");
        Company updatedData = buildCompany(null, "Beta", "900100101", "Retail");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.existsByName("Beta")).thenReturn(true);

        DuplicateCompanyException exception = assertThrows(DuplicateCompanyException.class,
                () -> companyService.update(1L, updatedData));

        assertEquals("Ya existe una empresa con nombre: Beta", exception.getMessage());
        verify(companyRepository).findById(1L);
        verify(companyRepository).existsByName("Beta");
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void updateShouldThrowWhenNitAlreadyExists() {
        Company existingCompany = buildCompany(1L, "Alpha", "900100100", "Tech");
        Company updatedData = buildCompany(null, "Alpha", "900200200", "Retail");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.existsByNit("900200200")).thenReturn(true);

        DuplicateCompanyException exception = assertThrows(DuplicateCompanyException.class,
                () -> companyService.update(1L, updatedData));

        assertEquals("Ya existe una empresa con NIT: 900200200", exception.getMessage());
        verify(companyRepository).findById(1L);
        verify(companyRepository).existsByNit("900200200");
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void deleteShouldRemoveExistingCompany() {
        Company existingCompany = buildCompany(1L, "Alpha", "900100100", "Tech");
        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));

        companyService.delete(1L);

        verify(companyRepository).findById(1L);
        verify(companyRepository).delete(existingCompany);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void deleteShouldThrowWhenCompanyDoesNotExist() {
        when(companyRepository.findById(88L)).thenReturn(Optional.empty());

        CompanyNotFoundException exception = assertThrows(CompanyNotFoundException.class, () -> companyService.delete(88L));

        assertEquals("Empresa no encontrada con ID: 88", exception.getMessage());
        verify(companyRepository).findById(88L);
        verifyNoMoreInteractions(companyRepository);
    }

    @Test
    void deleteShouldCallRepositoryDeleteExactlyOnce() {
        Company existingCompany = buildCompany(5L, "Delta", "900500500", "Logistics");
        when(companyRepository.findById(5L)).thenReturn(Optional.of(existingCompany));

        companyService.delete(5L);

        verify(companyRepository, times(1)).delete(existingCompany);
        verify(companyRepository).findById(5L);
        verifyNoMoreInteractions(companyRepository);
    }

    private Company buildCompany(Long id, String name, String nit, String industry) {
        Company company = new Company();
        company.setId(id);
        company.setName(name);
        company.setNit(nit);
        company.setIndustry(industry);
        return company;
    }
}
