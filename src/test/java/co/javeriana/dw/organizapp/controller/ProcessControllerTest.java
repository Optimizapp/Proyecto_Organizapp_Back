package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.ProcessRequestDto;
import co.javeriana.dw.organizapp.dto.ProcessResponseDto;
import co.javeriana.dw.organizapp.service.ProcessService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProcessController.class)
class ProcessControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
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

        when(processService.create(any())).thenReturn(new ProcessResponseDto());

        mockMvc.perform(post("/api/processes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void update() throws Exception {
        ProcessRequestDto request = new ProcessRequestDto();

        when(processService.update(eq(1L), any())).thenReturn(new ProcessResponseDto());

        mockMvc.perform(put("/api/processes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void delete() throws Exception {
        doNothing().when(processService).delete(1L);

        mockMvc.perform(delete("/api/processes/1"))
                .andExpect(status().isNoContent());
    }
}
