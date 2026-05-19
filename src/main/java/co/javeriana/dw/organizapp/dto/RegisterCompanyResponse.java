package co.javeriana.dw.organizapp.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterCompanyResponse {
    private CompanyResponseDto company;
    private UserResponseDto adminUser;
    private List<RoleResponseDto> roles;
    private PoolResponse defaultPool;
}
