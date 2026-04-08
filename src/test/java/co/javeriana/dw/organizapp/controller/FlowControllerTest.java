package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.FlowRequestDto;
import co.javeriana.dw.organizapp.dto.FlowResponseDto;
import co.javeriana.dw.organizapp.service.FlowService;
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
class FlowControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlowService flowService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllFlows() throws Exception {
        when(flowService.findAll()).thenReturn(List.of(new FlowResponseDto()));

        mockMvc.perform(get("/api/flows"))
                .andExpect(status().isOk());
    }

    @Test
    void getFlowsByVersionId() throws Exception {
        when(flowService.findByVersionId(1L)).thenReturn(List.of(new FlowResponseDto()));

        mockMvc.perform(get("/api/flows")
                        .param("versionId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getFlowById() throws Exception {
        when(flowService.findById(1L)).thenReturn(new FlowResponseDto());

        mockMvc.perform(get("/api/flows/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createFlow() throws Exception {
        FlowRequestDto request = new FlowRequestDto();

        // 🔥 CAMPOS OBLIGATORIOS
        request.setVersionId(1L);
        request.setOriginNodeId(10L);
        request.setDestinationNodeId(20L);

        // opcionales
        request.setCondicion("condición test");
        request.setEtiqueta("etiqueta test");

        when(flowService.create(any())).thenReturn(new FlowResponseDto());

        mockMvc.perform(post("/api/flows")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateFlow() throws Exception {
        FlowRequestDto request = new FlowRequestDto();

        // 🔥 CAMPOS OBLIGATORIOS
        request.setVersionId(1L);
        request.setOriginNodeId(10L);
        request.setDestinationNodeId(20L);

        // opcionales
        request.setCondicion("update condición");
        request.setEtiqueta("update etiqueta");

        when(flowService.update(eq(1L), any())).thenReturn(new FlowResponseDto());

        mockMvc.perform(put("/api/flows/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFlow() throws Exception {
        doNothing().when(flowService).delete(1L);

        mockMvc.perform(delete("/api/flows/1"))
                .andExpect(status().isNoContent());
    }
}