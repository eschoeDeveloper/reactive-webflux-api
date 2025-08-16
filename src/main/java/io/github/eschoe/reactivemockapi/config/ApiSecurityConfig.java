package io.github.eschoe.reactivemockapi.config;

import io.github.eschoe.reactivemockapi.security.*;
import io.github.eschoe.reactivemockapi.service.user.ApiUserRoleService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class ApiSecurityConfig {

    private final List<String> allowMethods = Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    private final List<String> allowHeaders = Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With", "X-Refresh-Token");
    private final String[] whitePathList = new String[]{"/h2-console",
            "/api/v3/api-docs/**",
            "/api/swagger-ui/**",
            "/api/swagger-ui.html",
            "/api/swagger-resources/**",
            "/api/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/api/auth/**",
            "/favicon.ico"};

    /**
     * Spring Security 기본 설정
     *
     * */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @Bean
    SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http, JwtAuthenticationConverter loginConverter,
                                                     JwtAuthWebFilter jwtAuthWebFilter, JwtUtil jwtUtil, ReactiveAuthenticationManager authManager) {

//        AuthenticationWebFilter loginFilter  = new AuthenticationWebFilter(authManager);
//
//        loginFilter.setServerAuthenticationConverter(loginConverter);
//        loginFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance());

//        // 로그인 성공 시 JWT 발급
//        loginFilter.setAuthenticationSuccessHandler((webFilterExchange, authentication) -> {
//            String username = authentication.getName();
//            String token = jwtUtil.generateAccessToken(username);
//            ServerWebExchange ex = webFilterExchange.getExchange();
//            var res = ex.getResponse();
//            res.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//            String body = "{\"token\":\"" + token + "\"}";
//            var buf = res.bufferFactory().wrap(body.getBytes(StandardCharsets.UTF_8));
//            return res.writeWith(Mono.just(buf));
//        });
//
//        // 로그인 실패 시 401
//        loginFilter.setAuthenticationFailureHandler((webFilterExchange, ex) -> {
//            var res = webFilterExchange.getExchange().getResponse();
//            res.setStatusCode(HttpStatus.UNAUTHORIZED);
//            return res.setComplete();
//        });

        return http
                .cors(cors -> {
                    cors.configurationSource(request -> {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:8090"));
                        // 특정 허용 메서드만 명시
                        corsConfiguration.setAllowedMethods(allowMethods);
                        // 특정 허용 헤더만 명시
                        corsConfiguration.setAllowedHeaders(allowHeaders);
                        corsConfiguration.setMaxAge(3600L);
                        return corsConfiguration;
                    });
                })
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(authManager)
                .authorizeExchange(exchange -> {
                    exchange.pathMatchers(whitePathList).permitAll()
                    .anyExchange().authenticated();
                })
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .exceptionHandling(e -> e.authenticationEntryPoint((swe, ex) ->
                        Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED))
                ))
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance()) // 상태 유지 안 함
//                .addFilterAt(loginFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(jwtAuthWebFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(JwtUtil jwtUtil, ApiUserDetailsService userDetailsService, ApiUserRoleService userRoleService, PasswordEncoder encoder) {
        return new JwtAuthenticationManager(jwtUtil, userDetailsService, userRoleService, encoder);
    }

    @Bean
    public JwtUtil jwtUtil(Key jwtSigningKey) {
        return new JwtUtil(jwtSigningKey, 3600_000, 7200_000); // 1시간
    }

}
