package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateLaneRequest {

    @NotBlank(message = "El nombre de la lane es obligatorio")
    @Size(max = 150, message = "El nombre de la lane no puede superar los 150 caracteres")
    private String name;

    @Size(max = 1000, message = "La descripcion no puede superar los 1000 caracteres")
    private String description;

    @PositiveOrZero(message = "La posicion debe ser positiva o cero")
    private Integer orderIndex;

    private Boolean active;

    @NotNull(message = "El pool asociado es obligatorio")
    private Long poolId;
}
