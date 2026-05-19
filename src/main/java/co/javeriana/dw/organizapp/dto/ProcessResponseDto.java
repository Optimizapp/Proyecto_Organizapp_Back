package co.javeriana.dw.organizapp.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProcessResponseDto {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String status;
    private Long companyId;
    private Long userId;
    private Long mainPoolId;
    private LocalDateTime createdAt;
}
