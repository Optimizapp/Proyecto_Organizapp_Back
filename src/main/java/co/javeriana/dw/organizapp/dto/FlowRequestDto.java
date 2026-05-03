package co.javeriana.dw.organizapp.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FlowRequestDto {
    @NotNull(message = "La version es obligatoria")
    private Long versionId;

    @JsonAlias("sourceNodeId")
    @NotNull(message = "El nodo origen es obligatorio")
    private Long originNodeId;

    @JsonAlias("targetNodeId")
    @NotNull(message = "El nodo destino es obligatorio")
    private Long destinationNodeId;

    private String type;

    @JsonAlias("conditionExpression")
    @Size(max = 500, message = "La condicion no puede superar los 500 caracteres")
    private String condicion;

    @JsonAlias("label")
    @Size(max = 100, message = "La etiqueta no puede superar los 100 caracteres")
    private String etiqueta;
}
