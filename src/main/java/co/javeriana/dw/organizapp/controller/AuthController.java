package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.auth.LoginRequest;
import co.javeriana.dw.organizapp.dto.auth.LoginResponse;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.security.JwtProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getContrasenaHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }

        if (!Boolean.TRUE.equals(user.getActivo())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Usuario inactivo"));
        }

        String token = jwtProvider.generateToken(user);

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .companyId(user.getCompany().getId())
                .companyName(user.getCompany().getName())
                .rolNombre(user.getRol().getNombre())
                .build();

        return ResponseEntity.ok(response);
    }
}
