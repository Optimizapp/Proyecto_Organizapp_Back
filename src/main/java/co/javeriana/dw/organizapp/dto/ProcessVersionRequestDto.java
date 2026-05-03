package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProcessVersionRequestDto {
    @NotNull(message = "El proceso es obligatorio")
    private Long processId;

    @NotNull(message = "El numero de version es obligatorio")
    private Integer numeroVersion;

    @NotBlank(message = "El estado de la version es obligatorio")
    private String estado;

    @NotNull(message = "El creador de la version es obligatorio")
    private Long createdByUserId;
}
