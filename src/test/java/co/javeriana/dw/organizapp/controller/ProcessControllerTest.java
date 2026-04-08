package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.ProcessRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.service.ProcessService;
import co.javeriana.dw.thymeleaf.ThymeleafApplication;
import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ThymeleafApplication.class)
@AutoConfigureMockMvc
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcessService processService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll() throws Exception {
        when(processService.findAll()).thenReturn(List.of(new ProcessResponseDto()));

        mockMvc.perform(get("/api/processes"))
                .andExpect(status().isOk());
    }

    @Test
    void getById() throws Exception {
        when(processService.findById(1L)).thenReturn(new ProcessResponseDto());

        mockMvc.perform(get("/api/processes/1"))
                .andExpect(status().isOk());
    }

    @Test
    void create() throws Exception {
        ProcessRequestDto request = new ProcessRequestDto();

        // 🔥 TODOS los campos obligatorios
        request.setName("Proceso Test");
        request.setDescription("Descripción válida");
        request.setStatus("ACTIVE");
        request.setCompanyId(1L);
        request.setUserId(1L);

        when(processService.create(any())).thenReturn(new ProcessResponseDto());

        mockMvc.perform(post("/api/processes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void update() throws Exception {
        ProcessRequestDto request = new ProcessRequestDto();

        // 🔥 TODOS los campos obligatorios
        request.setName("Proceso Update");
        request.setDescription("Descripción update");
        request.setStatus("ACTIVE");
        request.setCompanyId(1L);
        request.setUserId(1L);

        when(processService.update(eq(1L), any())).thenReturn(new ProcessResponseDto());

        mockMvc.perform(put("/api/processes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void delete() throws Exception {
        doNothing().when(processService).delete(1L);

        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/api/processes/1")
        ).andExpect(status().isNoContent());
    }
}