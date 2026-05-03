package co.javeriana.dw.organizapp.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import co.javeriana.dw.organizapp.dto.UserResponseDto;
import co.javeriana.dw.organizapp.exception.DuplicateResourceException;
import co.javeriana.dw.organizapp.exception.GlobalExceptionHandler;
import co.javeriana.dw.organizapp.service.UserService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class UserControllerTest {

    private UserService userService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new UserController(userService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createUserReturnsCreatedWhenRequestIsValid() throws Exception {
        UserResponseDto response = new UserResponseDto();
        response.setId(10L);
        response.setName("Diego");
        response.setEmail("diego@example.com");
        response.setCompanyId(1L);
        response.setRoleId(2L);
        response.setRoleNombre("Editor");
        response.setActive(true);
        response.setCreatedAt(LocalDateTime.now());
        when(userService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Diego",
                                  "email":"diego@example.com",
                                  "password":"password123",
                                  "companyId":1,
                                  "roleId":2
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.email").value("diego@example.com"))
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.contrasenaHash").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void createUserReturnsConflictWhenEmailIsDuplicated() throws Exception {
        when(userService.create(any()))
                .thenThrow(new DuplicateResourceException("Ya existe un usuario con correo: diego@example.com"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name":"Diego",
                                  "email":"diego@example.com",
                                  "password":"password123",
                                  "companyId":1,
                                  "roleId":2
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe un usuario con correo: diego@example.com"));
    }
}
