package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePoolRequest {

    @NotBlank(message = "El nombre del pool es obligatorio")
    @Size(max = 150, message = "El nombre del pool no puede superar los 150 caracteres")
    private String name;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    private String description;

    @NotNull(message = "El estado activo del pool es obligatorio")
    private Boolean active;

    @NotNull(message = "La empresa asociada es obligatoria")
    private Long companyId;
}
