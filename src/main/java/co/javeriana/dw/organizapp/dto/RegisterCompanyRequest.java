package co.javeriana.dw.organizapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterCompanyRequest {
    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(max = 100, message = "El nombre de la empresa no puede superar los 100 caracteres")
    private String companyName;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 20, message = "El NIT no puede superar los 20 caracteres")
    private String nit;

    @NotBlank(message = "El correo de contacto es obligatorio")
    @Email(message = "El correo de contacto debe tener un formato valido")
    @Size(max = 150, message = "El correo de contacto no puede superar los 150 caracteres")
    private String contactEmail;

    @Size(max = 100, message = "La industria no puede superar los 100 caracteres")
    private String industry;

    @NotBlank(message = "El nombre del administrador es obligatorio")
    @Size(max = 100, message = "El nombre del administrador no puede superar los 100 caracteres")
    private String adminName;

    @NotBlank(message = "El correo del administrador es obligatorio")
    @Email(message = "El correo del administrador debe tener un formato valido")
    @Size(max = 150, message = "El correo del administrador no puede superar los 150 caracteres")
    private String adminEmail;

    @NotBlank(message = "La contraseña del administrador es obligatoria")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
    private String adminPassword;
}
