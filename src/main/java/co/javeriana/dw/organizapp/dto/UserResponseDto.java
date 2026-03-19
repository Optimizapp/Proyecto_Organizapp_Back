package co.javeriana.dw.organizapp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data

public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Long companyId;
    private LocalDateTime createdAt;
}