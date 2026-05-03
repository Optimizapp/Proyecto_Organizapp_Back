package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateCompanyRequest {
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String name;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 20, message = "El NIT no puede superar los 20 caracteres")
    private String nit;

    @Size(max = 100, message = "La industria no puede superar los 100 caracteres")
    private String industry;

    @Email(message = "El correo de contacto debe tener un formato valido")
    @Size(max = 150, message = "El correo de contacto no puede superar los 150 caracteres")
    private String contactEmail;
}
