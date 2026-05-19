package co.javeriana.dw.organizapp.dto;

import lombok.Data;

@Data
public class RoleResponseDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long processId;
    private Long companyId;
}
