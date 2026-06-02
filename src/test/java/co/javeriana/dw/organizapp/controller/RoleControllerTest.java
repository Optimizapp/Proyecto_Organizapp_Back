package co.javeriana.dw.organizapp.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.exception.GlobalExceptionHandler;
import co.javeriana.dw.organizapp.service.RoleService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RoleControllerTest {

    private RoleService roleService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        roleService = mock(RoleService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new RoleController(roleService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getRolesReturnsRolesFilteredByCompanyId() throws Exception {
        RoleResponseDto admin = new RoleResponseDto();
        admin.setId(10L);
        admin.setNombre("ADMIN");
        admin.setDescripcion("Administrador de empresa");
        admin.setCompanyId(1L);

        when(roleService.findByCompanyId(eq(1L), eq(null))).thenReturn(List.of(admin));

        mockMvc.perform(get("/api/roles").requestAttr("companyId", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].nombre").value("ADMIN"))
                .andExpect(jsonPath("$[0].companyId").value(1))
                .andExpect(jsonPath("$[0].processId").doesNotExist());
    }
}
