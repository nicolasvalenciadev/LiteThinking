package com.litethinking.auth.infrastructure.web.controller;

import com.litethinking.auth.domain.port.in.AuthUseCase;
import com.litethinking.auth.infrastructure.web.dto.LoginRequestDTO;
import com.litethinking.auth.infrastructure.web.dto.LoginResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        String token = authUseCase.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(authUseCase.validateToken(token));
    }
}
