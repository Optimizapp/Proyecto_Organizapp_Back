package co.javeriana.dw.organizapp.dto;

import lombok.Data;

@Data
public class NodeResponseDto {
    private Long id;
    private Long versionId;
    private String tipo;
    private String nombre;
    private String descripcion;
    private Float x;
    private Float y;
}
