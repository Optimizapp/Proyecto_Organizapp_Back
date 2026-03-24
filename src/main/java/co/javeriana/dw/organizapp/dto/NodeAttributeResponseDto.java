package co.javeriana.dw.organizapp.dto;

import lombok.Data;

@Data
public class NodeAttributeResponseDto {
    private Long id;
    private Long nodeId;
    private String clave;
    private String valor;
    private String tipo;
}
