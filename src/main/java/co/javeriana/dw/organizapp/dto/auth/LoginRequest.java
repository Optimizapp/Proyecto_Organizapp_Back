package co.javeriana.dw.organizapp.dto.auth;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "El correo es obligatorio")
    @JsonAlias({"adminEmail"})
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @JsonAlias({"adminPassword"})
    private String password;
}
