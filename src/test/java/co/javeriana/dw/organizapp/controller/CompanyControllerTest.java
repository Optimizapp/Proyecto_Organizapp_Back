package co.javeriana.dw.organizapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.javeriana.dw.organizapp.dto.CompanyResponseDto;
import co.javeriana.dw.organizapp.dto.PoolResponse;
import co.javeriana.dw.organizapp.dto.RegisterCompanyResponse;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.GlobalExceptionHandler;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.service.CompanyService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CompanyControllerTest {

    private CompanyService companyService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        companyService = mock(CompanyService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new CompanyController(companyService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createCompanyReturnsCreatedWhenRequestIsValid() throws Exception {
        CompanyResponseDto response = new CompanyResponseDto(1L, "Acme", "900123", "Tech", "contact@acme.com");
        when(companyService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Acme","nit":"900123","industry":"Tech","contactEmail":"contact@acme.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Acme"))
                .andExpect(jsonPath("$.contactEmail").value("contact@acme.com"));
    }

    @Test
    void registerCompanyReturnsCreatedWithCompanyAdminAndBaseRoles() throws Exception {
        CompanyResponseDto company = new CompanyResponseDto(1L, "Acme", "900123", "Tech", "contact@acme.com");
        UserResponseDto admin = new UserResponseDto();
        admin.setId(5L);
        admin.setName("Admin");
        admin.setEmail("adminEmail@gmail.com");
        admin.setCompanyId(1L);
        admin.setRoleId(10L);
        admin.setRoleNombre("ADMIN");
        admin.setActive(true);
        RoleResponseDto adminRole = new RoleResponseDto();
        adminRole.setId(10L);
        adminRole.setNombre("ADMIN");
        adminRole.setCompanyId(1L);
        PoolResponse defaultPool = new PoolResponse();
        defaultPool.setId(30L);
        defaultPool.setName("Acme");
        defaultPool.setCompanyId(1L);
        defaultPool.setActive(true);
        when(companyService.register(any()))
                .thenReturn(new RegisterCompanyResponse(company, admin, List.of(adminRole), defaultPool));

        mockMvc.perform(post("/api/companies/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "companyName":"Acme",
                                  "nit":"900123",
                                  "contactEmail":"contact@acme.com",
                                  "industry":"Tech",
                                  "adminName":"Admin",
                                  "adminEmail":"adminEmail@gmail.com",
                                  "adminPassword":"12345678"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.company.id").value(1))
                .andExpect(jsonPath("$.adminUser.email").value("adminEmail@gmail.com"))
                .andExpect(jsonPath("$.adminUser.password").doesNotExist())
                .andExpect(jsonPath("$.adminUser.contrasenaHash").doesNotExist())
                .andExpect(jsonPath("$.roles[0].nombre").value("ADMIN"))
                .andExpect(jsonPath("$.defaultPool.name").value("Acme"));
    }

    @Test
    void createCompanyReturnsConflictWhenNitIsDuplicated() throws Exception {
        when(companyService.create(any()))
                .thenThrow(new DuplicateResourceException("Ya existe una empresa con NIT: 900123"));

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Acme","nit":"900123","industry":"Tech"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe una empresa con NIT: 900123"))
                .andExpect(jsonPath("$.path").value("/api/companies"));
    }

    @Test
    void getCompanyReturnsApiErrorResponseWhenNotFound() throws Exception {
        when(companyService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Empresa no encontrada con ID: 99"));

        mockMvc.perform(get("/api/companies/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Empresa no encontrada con ID: 99"))
                .andExpect(jsonPath("$.path").value("/api/companies/99"));
    }
}
