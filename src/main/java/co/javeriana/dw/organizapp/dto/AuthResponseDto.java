package co.javeriana.dw.organizapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String userName;
    private Long companyId;
    private String companyName;
    private java.util.List<String> roles;
}
