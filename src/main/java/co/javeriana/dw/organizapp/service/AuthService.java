package co.javeriana.dw.organizapp.service;

import co.javeriana.dw.organizapp.dto.AuthResponseDto;
import co.javeriana.dw.organizapp.dto.AuthRequestDto;

public interface AuthService {
    AuthResponseDto login(AuthRequestDto request);
}
