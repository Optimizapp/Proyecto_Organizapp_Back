package co.javeriana.dw.organizapp.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CommentResponseDto {
    private Long id;
    private Long versionId;
    private Long userId;
    private String contenido;
    private LocalDateTime createdAt;
}
