package co.javeriana.dw.organizapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.GlobalExceptionHandler;
import co.javeriana.dw.organizapp.service.ProcessService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProcessControllerTest {

    private ProcessService processService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        processService = mock(ProcessService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProcessController(processService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createProcessReturnsCreatedWhenRequestIsValid() throws Exception {
        ProcessResponseDto response = new ProcessResponseDto();
        response.setId(20L);
        response.setName("Onboarding");
        response.setDescription("Employee onboarding");
        response.setCategory("HR");
        response.setStatus("DRAFT");
        response.setCompanyId(1L);
        response.setUserId(10L);
        response.setCreatedAt(LocalDateTime.now());
        when(processService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/processes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Onboarding",
                                  "description":"Employee onboarding",
                                  "category":"HR",
                                  "status":"DRAFT",
                                  "companyId":1,
                                  "userId":10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.category").value("HR"))
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }

    @Test
    void createProcessReturnsBadRequestWhenStatusIsInvalid() throws Exception {
        when(processService.create(any()))
                .thenThrow(new BusinessRuleException("Estado de proceso invalido: UNKNOWN"));

        mockMvc.perform(post("/api/processes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Onboarding",
                                  "description":"Employee onboarding",
                                  "status":"UNKNOWN",
                                  "companyId":1,
                                  "userId":10
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Estado de proceso invalido: UNKNOWN"));
    }

    @Test
    void getProcessesAcceptsCompanyAndStatusFilters() throws Exception {
        when(processService.findAll(eq(1L), eq("ACTIVE"))).thenReturn(List.of());

        mockMvc.perform(get("/api/processes")
                        .param("companyId", "1")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk());
    }
}
