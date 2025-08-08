package io.github.eschoe.reactivemockapi.config;


import io.github.eschoe.reactivemockapi.security.JwtUtil;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;

@Configuration
public class JwtConfig {

    @Bean
    public Key jwtSigningKey() {
        // 안전한 256비트 키 생성
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

}
