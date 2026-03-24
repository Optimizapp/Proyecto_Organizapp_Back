package co.javeriana.dw.organizapp.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ProcessVersionResponseDto {
    private Long id;
    private Long processId;
    private Integer numeroVersion;
    private String estado;
    private Long createdByUserId;
    private LocalDateTime createdAt;
}
