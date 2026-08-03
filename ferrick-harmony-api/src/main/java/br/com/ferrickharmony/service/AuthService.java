package br.com.ferrickharmony.service;

import br.com.ferrickharmony.dto.auth.AuthenticationDataDTO;
import br.com.ferrickharmony.model.User;
import br.com.ferrickharmony.security.TokenService;
import br.com.ferrickharmony.security.dto.TokenJWTDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public TokenJWTDTO login(AuthenticationDataDTO credentials) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(credentials.email(), credentials.password());
        var authentication = authenticationManager.authenticate(authenticationToken);
        var tokenJwt = tokenService.generateToken((User) Objects.requireNonNull(authentication.getPrincipal()));
        return new TokenJWTDTO(tokenJwt);
    }
}
