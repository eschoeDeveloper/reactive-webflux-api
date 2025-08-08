package io.github.eschoe.reactivemockapi.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

public class JwtUtil {

    private final Key key;
    private final long expirationMillis;

    public JwtUtil(final Key key, final long expirationMillis) {
        this.key = key;
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.ofEpochMilli(System.currentTimeMillis())))
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public String validateAndGetUsername(String token) {

        try {

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

        } catch (JwtException e) {
            return null;
        }

    }

}
