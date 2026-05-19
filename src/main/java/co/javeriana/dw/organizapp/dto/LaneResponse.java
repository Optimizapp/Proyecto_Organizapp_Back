package co.javeriana.dw.organizapp.dto;

import lombok.Data;

@Data
public class LaneResponse {
    private Long id;
    private String name;
    private String description;
    private Integer orderIndex;
    private Boolean active;
    private Long poolId;
}
