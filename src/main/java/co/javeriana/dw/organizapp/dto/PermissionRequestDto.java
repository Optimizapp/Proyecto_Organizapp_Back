package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionRequestDto {
    @NotBlank(message = "El codigo del permiso es obligatorio")
    @Size(max = 100, message = "El codigo del permiso no puede superar los 100 caracteres")
    private String codigo;

    @Size(max = 255, message = "La descripcion no puede superar los 255 caracteres")
    private String descripcion;

    @NotNull(message = "El rol asociado es obligatorio")
    private Long roleId;
}
