package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.NodeRequestDto;
import co.javeriana.dw.organizapp.dto.NodeResponseDto;
import co.javeriana.dw.organizapp.service.NodeService;
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
class NodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NodeService nodeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllNodes() throws Exception {
        when(nodeService.findAll()).thenReturn(List.of(new NodeResponseDto()));

        mockMvc.perform(get("/api/nodes"))
                .andExpect(status().isOk());
    }

    @Test
    void getNodesByVersionId() throws Exception {
        when(nodeService.findByVersionId(1L)).thenReturn(List.of(new NodeResponseDto()));

        mockMvc.perform(get("/api/nodes")
                        .param("versionId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getNodeById() throws Exception {
        when(nodeService.findById(1L)).thenReturn(new NodeResponseDto());

        mockMvc.perform(get("/api/nodes/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createNode() throws Exception {
        NodeRequestDto request = new NodeRequestDto();

        request.setVersionId(1L);
        request.setTipo("TASK");
        request.setNombre("Nodo Test");
        request.setX(10f);
        request.setY(20f);

        request.setDescripcion("Descripción del nodo");

        when(nodeService.create(any())).thenReturn(new NodeResponseDto());

        mockMvc.perform(post("/api/nodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateNode() throws Exception {
        NodeRequestDto request = new NodeRequestDto();

        request.setVersionId(1L);
        request.setTipo("TASK");
        request.setNombre("Nodo Update");
        request.setX(15f);
        request.setY(25f);

        request.setDescripcion("Descripción update");

        when(nodeService.update(eq(1L), any())).thenReturn(new NodeResponseDto());

        mockMvc.perform(put("/api/nodes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteNode() throws Exception {
        doNothing().when(nodeService).delete(1L);

        mockMvc.perform(delete("/api/nodes/1"))
                .andExpect(status().isNoContent());
    }
}