package com.alhakim.issuetracker.service;

import com.alhakim.issuetracker.config.JwtConfig;
import com.alhakim.issuetracker.dto.MyUserDetails;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final JwtConfig jwtConfig;

    @Override
    public String generateToken(MyUserDetails user) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getJwtSecret().getBytes());
        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getJwtExpiration()))
                .signWith(key)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getJwtSecret().getBytes());
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtConfig.getJwtSecret().getBytes());
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
