package co.javeriana.dw.organizapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private Long roleId;
    private String roleNombre;
    private Long companyId;
    private Boolean active;
    private LocalDateTime createdAt;
}
