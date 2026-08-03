package br.com.ferrickharmony.security;

import br.com.ferrickharmony.exception.BusinessException;
import br.com.ferrickharmony.model.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;

import static br.com.ferrickharmony.enums.ErrorKey.INVALID_JWT_TOKEN;

@Service
public class TokenService {

    @Value("${token.jwt.secret}")
    private String secret;

    public String generateToken(User user) {
        var algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("ferrick-harmony")
                .withSubject(user.getEmail())
                .withExpiresAt(dataExpiration())
                .withClaim("id", user.getId().toString())
                .sign(algorithm);
    }

    public String getSubject(String token) {
        try {
            var algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("ferrick-harmony")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception){
            throw new BusinessException(INVALID_JWT_TOKEN.getKey());
        }
    }

    private Instant dataExpiration() {
        return LocalDateTime.now().plusHours(12).toInstant(ZoneOffset.of("-03:00"));
    }
}
