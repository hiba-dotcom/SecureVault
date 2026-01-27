package com.securevault.passwordservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    // 🔐 Clé de signature UNIQUE
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 🔍 Extraire le username
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    // 🔍 Extraire tous les claims
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token) // ⬅️ PAS de replace("Bearer ")
                .getBody();
    }

    // ✅ Valider le token (signature + format)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
