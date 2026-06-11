package com.skillswap.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

public class JWTService {

    private static final String SECRET = "skillswap_secret";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24;

    public String generateToken(Integer idUsuario, String email) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.create()
                .withSubject(email)
                .withClaim("id", idUsuario)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION))
                .sign(algorithm);
    }

    public String validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getSubject();
    }

    public Integer getIdUsuarioFromToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET);

        return JWT.require(algorithm)
                .build()
                .verify(token)
                .getClaim("id")
                .asInt();
    }

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            return null;

        return authorizationHeader.replace("Bearer ", "").trim();
    }
}