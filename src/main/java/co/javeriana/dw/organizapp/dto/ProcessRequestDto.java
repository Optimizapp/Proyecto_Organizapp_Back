package co.javeriana.dw.organizapp.dto;


import lombok.Data;
import jakarta.validation.constraints.*;

@Data
@Deprecated(since = "0.0.1", forRemoval = false)
public class ProcessRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;
    
    @Size(max = 1000)
    private String description;
    
    @NotBlank(message = "El estado es obligatorio")
    private String status; // Se recibe como String y se valida/convierte en el Service
    
    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long companyId;
    
    @NotNull(message = "El ID del usuario responsable es obligatorio")
    private Long userId;
}
