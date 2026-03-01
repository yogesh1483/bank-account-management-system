package com.bank.management.app.util;

import com.bank.management.app.entity.Customer;
import com.bank.management.app.exception.ResourceNotFoundException;
import com.bank.management.app.repository.CustomerRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final CustomerRepository customerRepository;

    private String jwtSecret = "bXlTdXBlclNlY3JldEtleUZvckpXVEJhbmtBcHAxMjM0NTY3ODkw";
    private final long jwtExpirationDate = 3600000; // 1 hour

    @PostConstruct
    public void init() {
        this.jwtSecret = generateSecretKey(); // generated once on startup
    }

    // 🔐 TOKEN GENERATION
    public String generateToken(org.springframework.security.core.Authentication authentication) {

        String username = authentication.getName();

        Customer customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationDate);

        return Jwts.builder()
                .subject(username)
                .claim("role", customer.getRole())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔑 SIGNING KEY
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // 🔐 RANDOM 256-BIT SECRET
    private String generateSecretKey() {
        byte[] key = new byte[32]; // 256 bits
        new SecureRandom().nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }

    // 👤 USERNAME
    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 🎭 ROLE
    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // ✅ VALIDATION
    public boolean validateToken(String token) {
        Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parse(token);
        return true;
    }
}
