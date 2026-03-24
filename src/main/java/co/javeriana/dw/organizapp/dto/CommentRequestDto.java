package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDto {
    @NotNull(message = "La version es obligatoria")
    private Long versionId;

    @NotNull(message = "El usuario es obligatorio")
    private Long userId;

    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(max = 2000, message = "El contenido no puede superar los 2000 caracteres")
    private String contenido;
}
