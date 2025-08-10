package io.github.eschoe.reactivemockapi.config;

import io.github.eschoe.reactivemockapi.security.JsonRequestConverter;
import io.github.eschoe.reactivemockapi.security.JwtAuthWebFilter;
import io.github.eschoe.reactivemockapi.security.JwtAuthenticationManager;
import io.github.eschoe.reactivemockapi.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

@Configuration
@EnableWebFluxSecurity
public class ApiSecurityConfig {

    /**
     * Spring Security 기본 설정
     *
     * */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JsonRequestConverter loginConverter,
                                                     JwtAuthWebFilter jwtAuthWebFilter, JwtUtil jwtUtil, ReactiveAuthenticationManager authManager) {

        AuthenticationWebFilter loginFilter  = new AuthenticationWebFilter(authManager);

        loginFilter.setServerAuthenticationConverter(loginConverter);
        loginFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        // 로그인 성공 시 JWT 발급
        loginFilter.setAuthenticationSuccessHandler((webFilterExchange, authentication) -> {
            String username = authentication.getName();
            String token = jwtUtil.generateToken(username);
            ServerWebExchange ex = webFilterExchange.getExchange();
            var res = ex.getResponse();
            res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
            String body = "{\"token\":\"" + token + "\"}";
            var buf = res.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
            return res.writeWith(Mono.just(buf));
        });

        // 로그인 실패 시 401
        loginFilter.setAuthenticationFailureHandler((webFilterExchange, ex) -> {
            var res = webFilterExchange.getExchange().getResponse();
            res.setStatusCode(HttpStatus.UNAUTHORIZED);
            return res.setComplete();
        });

        return http
                .cors(cors -> {
                    cors.configurationSource(request -> {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:8090"));
                        // 특정 허용 메서드만 명시
                        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                        // 특정 허용 헤더만 명시
                        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
                        corsConfiguration.setMaxAge(3600L);
                        return corsConfiguration;
                    });
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange -> {
                    exchange.pathMatchers(
                            "/h2-console",
                        "/h2-console/**",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v2/api-docs",
                        "/swagger-resources/**",
                        "/webjars/**",
                        "/auth/**",
                        "/favicon.ico"
                    ).permitAll()
                    .anyExchange().authenticated();
                })
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // 상태 유지 안 함
                .addFilterAt(loginFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtAuthWebFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public ReactiveUserDetailsService reactiveUserDetailsService(PasswordEncoder encoder) {
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("password"))
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(JwtUtil jwtUtil) {
        return new JwtAuthenticationManager(jwtUtil);
    }

    @Bean
    public JwtUtil jwtUtil(Key jwtSigningKey) {
        return new JwtUtil(jwtSigningKey, 3600_000); // 1시간
    }

}
