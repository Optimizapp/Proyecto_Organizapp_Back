package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NodeAttributeRequestDto {
    @NotNull(message = "El nodo es obligatorio")
    private Long nodeId;

    @NotBlank(message = "La clave del atributo es obligatoria")
    @Size(max = 100, message = "La clave del atributo no puede superar los 100 caracteres")
    private String clave;

    @Size(max = 2000, message = "El valor del atributo no puede superar los 2000 caracteres")
    private String valor;

    @NotNull(message = "El tipo del atributo es obligatorio")
    private String tipo;
}
