package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.RoleRequestDto;
import co.javeriana.dw.organizapp.dto.RoleResponseDto;
import co.javeriana.dw.organizapp.service.RoleService;
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
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllRoles() throws Exception {
        when(roleService.findAll()).thenReturn(List.of(new RoleResponseDto()));

        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk());
    }

    @Test
    void getRolesByProcessId() throws Exception {
        when(roleService.findByProcessId(1L)).thenReturn(List.of(new RoleResponseDto()));

        mockMvc.perform(get("/api/roles")
                        .param("processId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    void getRoleById() throws Exception {
        when(roleService.findById(1L)).thenReturn(new RoleResponseDto());

        mockMvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createRole() throws Exception {
        RoleRequestDto request = new RoleRequestDto();

        request.setNombre("Admin");
        request.setProcessId(1L);

        request.setDescripcion("Rol administrador");

        when(roleService.create(any())).thenReturn(new RoleResponseDto());

        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateRole() throws Exception {
        RoleRequestDto request = new RoleRequestDto();

        
        request.setNombre("User");
        request.setProcessId(1L);

        request.setDescripcion("Rol usuario");

        when(roleService.update(eq(1L), any())).thenReturn(new RoleResponseDto());

        mockMvc.perform(put("/api/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteRole() throws Exception {
        doNothing().when(roleService).delete(1L);

        mockMvc.perform(delete("/api/roles/1"))
                .andExpect(status().isNoContent());
    }
}