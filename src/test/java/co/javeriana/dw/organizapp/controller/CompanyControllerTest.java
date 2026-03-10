package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.entity.Company;
import co.javeriana.dw.organizapp.exception.CompanyNotFoundException;
import co.javeriana.dw.organizapp.exception.DuplicateCompanyException;
import co.javeriana.dw.organizapp.exception.GlobalExceptionHandler;
import co.javeriana.dw.organizapp.service.CompanyService;
import org.hibernate.validator.HibernateValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CompanyControllerTest {

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyController companyController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(companyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void getAllCompaniesShouldReturnOk() throws Exception {
        when(companyService.findAll()).thenReturn(List.of(
                buildCompany(1L, "Alpha", "900100100", "Tech"),
                buildCompany(2L, "Beta", "900200200", "Finance")
        ));

        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Alpha"))
                .andExpect(jsonPath("$[1].nit").value("900200200"));
    }

    @Test
    void getCompanyByIdShouldReturnOk() throws Exception {
        when(companyService.findById(1L)).thenReturn(buildCompany(1L, "Alpha", "900100100", "Tech"));

        mockMvc.perform(get("/api/companies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alpha"));
    }

    @Test
    void createCompanyShouldReturnCreated() throws Exception {
        Company createdCompany = buildCompany(3L, "Gamma", "900300300", "Health");
        when(companyService.create(any(Company.class))).thenReturn(createdCompany);

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gamma\",\"nit\":\"900300300\",\"industry\":\"Health\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Gamma"));
    }

    @Test
    void updateCompanyShouldReturnOk() throws Exception {
        Company updatedCompany = buildCompany(1L, "Alpha Updated", "900100101", "Retail");
        when(companyService.update(eq(1L), any(Company.class))).thenReturn(updatedCompany);

        mockMvc.perform(put("/api/companies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alpha Updated\",\"nit\":\"900100101\",\"industry\":\"Retail\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alpha Updated"))
                .andExpect(jsonPath("$.industry").value("Retail"));
    }

    @Test
    void deleteCompanyShouldReturnNoContent() throws Exception {
        doNothing().when(companyService).delete(1L);

        mockMvc.perform(delete("/api/companies/1"))
                .andExpect(status().isNoContent());

        verify(companyService).delete(1L);
    }

    @Test
    void getCompanyByIdShouldReturnNotFoundWhenCompanyDoesNotExist() throws Exception {
        when(companyService.findById(99L)).thenThrow(new CompanyNotFoundException(99L));

        mockMvc.perform(get("/api/companies/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Empresa no encontrada con ID: 99"));
    }

    @Test
    void updateCompanyShouldReturnNotFoundWhenCompanyDoesNotExist() throws Exception {
        when(companyService.update(eq(99L), any(Company.class))).thenThrow(new CompanyNotFoundException(99L));

        mockMvc.perform(put("/api/companies/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Alpha Updated\",\"nit\":\"900100101\",\"industry\":\"Retail\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Empresa no encontrada con ID: 99"));
    }

    @Test
    void deleteCompanyShouldReturnNotFoundWhenCompanyDoesNotExist() throws Exception {
        org.mockito.Mockito.doThrow(new CompanyNotFoundException(99L)).when(companyService).delete(99L);

        mockMvc.perform(delete("/api/companies/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Empresa no encontrada con ID: 99"));
    }

    @Test
    void createCompanyShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"nit\":\"\",\"industry\":\"Health\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields.name").value("El nombre es obligatorio"))
                .andExpect(jsonPath("$.fields.nit").value("El NIT es obligatorio"));
    }

    @Test
    void updateCompanyShouldReturnBadRequestWhenBodyIsInvalid() throws Exception {
        mockMvc.perform(put("/api/companies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"nit\":\"900100101\",\"industry\":\"Retail\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields.name").value("El nombre es obligatorio"));
    }

    @Test
    void createCompanyShouldReturnConflictWhenCompanyIsDuplicated() throws Exception {
        when(companyService.create(any(Company.class)))
                .thenThrow(new DuplicateCompanyException("Ya existe una empresa con NIT: 900300300"));

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Gamma\",\"nit\":\"900300300\",\"industry\":\"Health\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Ya existe una empresa con NIT: 900300300"));
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
