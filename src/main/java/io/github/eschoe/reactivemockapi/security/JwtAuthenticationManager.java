package io.github.eschoe.reactivemockapi.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtUtil jwtUtil;
    private final ApiUserDetailsService apiUserDetailsService;
    private final ApiUserRoleService apiUserRoleService;
    private final PasswordEncoder passwordEncoder;

    public JwtAuthenticationManager(JwtUtil jwtUtil, ApiUserDetailsService apiUserDetailsService, ApiUserRoleService apiUserRoleService, PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.apiUserDetailsService = apiUserDetailsService;
        this.apiUserRoleService = apiUserRoleService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        return Mono.defer(() -> {
            String cred = authentication.getCredentials() == null ? "" : authentication.getCredentials().toString();

            // 1) 먼저 JWT 시도
            try {
                
                String jwtUsername = jwtUtil.validateAndGetUsername(cred); // 서명/만료 검증 + username (유효하지 않으면 null 또는 예외)

                if (jwtUsername != null && !jwtUsername.isBlank()) {
                    List<GrantedAuthority> authorities = extractAuthoritiesFromJwtSafely(jwtUsername);
                    return Mono.just(new UsernamePasswordAuthenticationToken(jwtUsername, null, authorities));
                }
                
            } catch (CredentialsExpiredException e) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "요청 만료"));
            } catch (Exception ignore) {
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ignore.getMessage()));
            }

            // 2) JWT가 아니면 username/password 검증
            String username = authentication.getName();
            String rawPassword = cred;

            System.out.println("authentication.getName() = " + authentication.getName());
            System.out.println("rawPassword: " + rawPassword);

            return apiUserDetailsService.findByUsername(username)
                    .switchIfEmpty(Mono.error(new BadCredentialsException("잘못된 인증입니다.")))
                    .flatMap(ud -> {

                        System.out.println("ud = " + ud.getPassword());
                        System.out.println("rawPassword = " + rawPassword);

                        if (!passwordEncoder.matches(rawPassword, ud.getPassword())) {
                            return Mono.error(new BadCredentialsException("잘못된 인증입니다."));
                        }
                        return Mono.just(new UsernamePasswordAuthenticationToken(
                                ud, null, ud.getAuthorities()));
                    });
        });

    }

    private List<GrantedAuthority> extractAuthoritiesFromJwtSafely(String username) {
        try {
            List<String> roles = apiUserRoleService.getAhthoritiesByUsername(username).collectList().block(); // 예: ["ROLE_USER","ROLE_ADMIN"]; 없으면 예외/null일 수 있음
            if (roles == null || roles.isEmpty()) roles = List.of("ROLE_USER");
            return roles.stream().distinct().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        } catch (Exception e) {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }

}
