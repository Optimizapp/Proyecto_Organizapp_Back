package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProcessRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    private String description;

    @Size(max = 100, message = "La categoria no puede superar los 100 caracteres")
    private String category;

    @NotBlank(message = "El estado es obligatorio")
    private String status;

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long companyId;

    @NotNull(message = "El ID del usuario responsable es obligatorio")
    private Long userId;

    private Long mainPoolId;
}
