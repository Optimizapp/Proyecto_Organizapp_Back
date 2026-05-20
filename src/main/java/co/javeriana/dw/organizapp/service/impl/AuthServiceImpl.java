package co.javeriana.dw.organizapp.service.impl;

import co.javeriana.dw.organizapp.dto.AuthRequestDto;
import co.javeriana.dw.organizapp.dto.AuthResponseDto;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.security.JwtUtil;
import co.javeriana.dw.organizapp.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtUtil jwtUtil,
                           UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(AuthRequestDto request) {
        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    request.getEmail(),
                                    request.getPassword()
                            )
                    );
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Credenciales invalidas", e);
        }

        // Usuario autenticado: buscamos la entidad User para obtener companyId y companyName
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Usuario no encontrado"));

        Long companyId = user.getCompany().getId();
        String companyName = user.getCompany().getName();
        String userName = user.getName();

        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), companyId);

        AuthResponseDto response = new AuthResponseDto(
                token,
                "Bearer",
                user.getId(),
                user.getEmail(),
                userName,
                companyId,
                companyName,
                List.of(user.getRol().getNombre())
        );

        return response;
    }
}
