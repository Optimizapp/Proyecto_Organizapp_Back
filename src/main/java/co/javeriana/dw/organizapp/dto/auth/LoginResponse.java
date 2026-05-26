package co.javeriana.dw.organizapp.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {

    private String token;
    @Builder.Default
    private String type = "Bearer";
    private Long userId;
    private String userName;
    private String userEmail;
    private Long companyId;
    private String companyName;
    private String rolNombre;   
}