package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NodeRequestDto {
    @NotNull(message = "La version es obligatoria")
    private Long versionId;

    @NotNull(message = "El tipo del nodo es obligatorio")
    private String tipo;

    @NotBlank(message = "El nombre del nodo es obligatorio")
    @Size(max = 150, message = "El nombre del nodo no puede superar los 150 caracteres")
    private String nombre;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    private String descripcion;

    @NotNull(message = "La coordenada x es obligatoria")
    @PositiveOrZero(message = "La coordenada x debe ser positiva o cero")
    private Float x;

    @NotNull(message = "La coordenada y es obligatoria")
    @PositiveOrZero(message = "La coordenada y debe ser positiva o cero")
    private Float y;
}
