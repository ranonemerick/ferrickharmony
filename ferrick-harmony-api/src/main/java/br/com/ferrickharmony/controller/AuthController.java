package br.com.ferrickharmony.controller;

import br.com.ferrickharmony.dto.auth.AuthenticationDataDTO;
import br.com.ferrickharmony.security.dto.TokenJWTDTO;
import br.com.ferrickharmony.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenJWTDTO> login(@RequestBody @Valid AuthenticationDataDTO credentials) {
        var auth = authService.login(credentials);
        return ResponseEntity.ok().body(auth);
    }

}
