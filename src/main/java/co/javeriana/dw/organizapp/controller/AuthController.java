package co.javeriana.dw.organizapp.controller;

import co.javeriana.dw.organizapp.dto.auth.LoginRequest;
import co.javeriana.dw.organizapp.dto.auth.LoginResponse;
import co.javeriana.dw.organizapp.entity.User;
import co.javeriana.dw.organizapp.repository.UserRepository;
import co.javeriana.dw.organizapp.security.JwtProvider;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final Counter loginSuccessCounter;
    private final Counter loginFailureCounter;

    public AuthController(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider,
            MeterRegistry meterRegistry) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.loginSuccessCounter = Counter.builder("auth.login.success")
                .description("Successful login attempts")
                .register(meterRegistry);
        this.loginFailureCounter = Counter.builder("auth.login.failure")
                .description("Failed login attempts")
                .register(meterRegistry);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            loginFailureCounter.increment();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getContrasenaHash())) {
            loginFailureCounter.increment();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Credenciales inválidas"));
        }

        if (!Boolean.TRUE.equals(user.getActivo())) {
            loginFailureCounter.increment();
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Usuario inactivo"));
        }

        String token = jwtProvider.generateToken(user);

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .userName(user.getName())
                .userEmail(user.getEmail())
                .companyId(user.getCompany().getId())
                .companyName(user.getCompany().getName())
                .rolNombre(user.getRol().getNombre())
                .build();

        loginSuccessCounter.increment();
        return ResponseEntity.ok(response);
    }
}
