package co.javeriana.dw.organizapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NodeRequestDto {
    @NotNull(message = "La version es obligatoria")
    private Long versionId;

    @JsonAlias("type")
    @NotBlank(message = "El tipo del nodo es obligatorio")
    private String tipo;

    private String gatewayType;

    @JsonAlias("name")
    @NotBlank(message = "El nombre del nodo es obligatorio")
    @Size(max = 150, message = "El nombre del nodo no puede superar los 150 caracteres")
    private String nombre;

    @JsonAlias("description")
    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    private String descripcion;

    @NotNull(message = "La coordenada x es obligatoria")
    @PositiveOrZero(message = "La coordenada x debe ser positiva o cero")
    private Float x;

    @NotNull(message = "La coordenada y es obligatoria")
    @PositiveOrZero(message = "La coordenada y debe ser positiva o cero")
    private Float y;

    @PositiveOrZero(message = "El ancho debe ser positivo o cero")
    private Float width;

    @PositiveOrZero(message = "El alto debe ser positivo o cero")
    private Float height;

    private Long laneId;
}
