package co.javeriana.dw.organizapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.javeriana.dw.organizapp.dto.ProcessVersionResponseDto;
import co.javeriana.dw.organizapp.exception.BusinessRuleException;
import co.javeriana.dw.organizapp.exception.GlobalExceptionHandler;
import co.javeriana.dw.organizapp.exception.ResourceNotFoundException;
import co.javeriana.dw.organizapp.service.ProcessVersionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProcessVersionControllerTest {

    private ProcessVersionService processVersionService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        processVersionService = mock(ProcessVersionService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new ProcessVersionController(processVersionService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getProcessVersionsAcceptsProcessIdFilter() throws Exception {
        ProcessVersionResponseDto response = response("BORRADOR");
        when(processVersionService.findByProcessId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/process-versions").param("processId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].processId").value(1))
                .andExpect(jsonPath("$[0].estado").value("BORRADOR"));
    }

    @Test
    void createProcessVersionReturnsCreatedWhenRequestIsValid() throws Exception {
        when(processVersionService.create(any())).thenReturn(response("BORRADOR"));

        mockMvc.perform(post("/api/process-versions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "processId":1,
                                  "numeroVersion":1,
                                  "estado":"BORRADOR",
                                  "createdByUserId":10
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.estado").value("BORRADOR"));
    }

    @Test
    void createProcessVersionReturnsNotFoundWhenProcessDoesNotExist() throws Exception {
        when(processVersionService.create(any()))
                .thenThrow(new ResourceNotFoundException("Proceso no encontrado con ID: 99"));

        mockMvc.perform(post("/api/process-versions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "processId":99,
                                  "numeroVersion":1,
                                  "estado":"BORRADOR",
                                  "createdByUserId":10
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Proceso no encontrado con ID: 99"));
    }

    @Test
    void createProcessVersionReturnsBadRequestWhenStatusIsInvalid() throws Exception {
        when(processVersionService.create(any()))
                .thenThrow(new BusinessRuleException("Estado de version de proceso invalido: INVALIDA"));

        mockMvc.perform(post("/api/process-versions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "processId":1,
                                  "numeroVersion":1,
                                  "estado":"INVALIDA",
                                  "createdByUserId":10
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Estado de version de proceso invalido: INVALIDA"));
    }

    @Test
    void publishProcessVersionReturnsPublishedVersion() throws Exception {
        when(processVersionService.publish(eq(100L))).thenReturn(response("PUBLICADA"));

        mockMvc.perform(post("/api/process-versions/100/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.estado").value("PUBLICADA"));
    }

    private static ProcessVersionResponseDto response(String status) {
        ProcessVersionResponseDto response = new ProcessVersionResponseDto();
        response.setId(100L);
        response.setProcessId(1L);
        response.setNumeroVersion(1);
        response.setEstado(status);
        response.setCreatedByUserId(10L);
        return response;
    }
}
