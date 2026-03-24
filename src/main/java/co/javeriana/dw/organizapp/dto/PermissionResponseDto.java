package co.javeriana.dw.organizapp.dto;

import lombok.Data;

@Data
public class PermissionResponseDto {
    private Long id;
    private String codigo;
    private String descripcion;
    private Long roleId;
}
