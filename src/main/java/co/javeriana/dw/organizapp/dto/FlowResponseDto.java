package co.javeriana.dw.organizapp.dto;

import lombok.Data;

@Data
public class FlowResponseDto {
    private Long id;
    private Long versionId;
    private Long originNodeId;
    private Long destinationNodeId;
    private String condicion;
    private String etiqueta;
}
