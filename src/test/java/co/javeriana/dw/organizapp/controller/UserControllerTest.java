package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.UserRequestDto;
import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.service.UserService;
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
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(List.of(new UserResponseDto()));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserById() throws Exception {
        when(userService.findById(1L)).thenReturn(new UserResponseDto());

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createUser() throws Exception {
        UserRequestDto request = new UserRequestDto();

        // 🔥 CAMPOS OBLIGATORIOS DEL DTO
        request.setName("Juan Perez");
        request.setEmail("juan@test.com");
        request.setRoleId(1L);
        request.setCompanyId(1L);

        when(userService.create(any())).thenReturn(new UserResponseDto());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void updateUser() throws Exception {
        UserRequestDto request = new UserRequestDto();

        // 🔥 CAMPOS OBLIGATORIOS DEL DTO
        request.setName("Juan Update");
        request.setEmail("update@test.com");
        request.setRoleId(1L);
        request.setCompanyId(1L);

        when(userService.update(eq(1L), any())).thenReturn(new UserResponseDto());

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}