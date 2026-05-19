package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Deprecated(since = "0.0.1", forRemoval = false)
public class UserRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo debe tener un formato valido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String email;

    @NotNull(message = "El ID del rol es obligatorio")
    private Long roleId;

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long companyId;
}
