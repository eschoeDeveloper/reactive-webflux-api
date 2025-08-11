package io.github.eschoe.reactivemockapi.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthWebFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthWebFilter(JwtUtil jwtUtil, ReactiveUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        return Mono.justOrEmpty(authHeader)
                .filter(h -> StringUtils.startsWithIgnoreCase(h, "Bearer "))
                .map(h -> h.substring(7))
                // 토큰 검증 중 예외나 null/blank → 빈으로 처리
                .flatMap(token -> Mono.fromCallable(() -> jwtUtil.validateAndGetUsername(token))
                        .onErrorReturn("") )
                .filter(StringUtils::isNotBlank)
                // 사용자 조회: not found/오류여도 체인 계속
                .flatMap(username -> userDetailsService.findByUsername(username)
                        .map(ud -> new UsernamePasswordAuthenticationToken(ud, null, ud.getAuthorities()))
                        .flatMap(auth -> chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth)))
                        .switchIfEmpty(chain.filter(exchange))              // 사용자 없음
                        .onErrorResume(e -> chain.filter(exchange))         // 조회 중 에러
                )
                // 위의 어느 단계에서든 조건 미충족 시 → 그냥 통과
                .switchIfEmpty(chain.filter(exchange));

    }

}
