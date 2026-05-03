package co.javeriana.dw.organizapp.dto;

import lombok.Data;

@Data
public class PoolResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Long companyId;
}
