package io.github.eschoe.reactivemockapi.controller;

import io.github.eschoe.reactivemockapi.dto.ApiUser;
import io.github.eschoe.reactivemockapi.dto.ApiUserPrincipal;
import io.github.eschoe.reactivemockapi.dto.auth.request.ApiLoginRequest;
import io.github.eschoe.reactivemockapi.dto.auth.response.LoginResponse;
import io.github.eschoe.reactivemockapi.repository.ApiUserRepository;
import io.github.eschoe.reactivemockapi.security.JwtAuthenticationManager;
import io.github.eschoe.reactivemockapi.security.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final ReactiveAuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(ReactiveAuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<LoginResponse>> login(
            @RequestBody ApiLoginRequest req
            ) {

        if(StringUtils.isBlank(req.getUserid()) || StringUtils.isBlank(req.getPassword())) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 사용자명/패스워드 입니다."));
        }

        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(req.getUserid(), req.getPassword());

        return Mono.defer(() -> authenticationManager.authenticate(authRequest)) // ★ 동기 예외 흡수
                .map(auth -> {
                    // ★ 캐스팅 금지: ApiUserPrincipal로 캐스팅하다가 ClassCastException 많이 납니다
                    String username = auth.getName();

                    String access = jwtUtil.generateAccessToken(username);
                    String refresh = jwtUtil.generateRefreshToken(username);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + access)
                            .header("X-Refresh-Token", refresh)
                            .body(new LoginResponse("OK"));
                })
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse("UNAUTHORIZED"))))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse("UNAUTHORIZED"))));

    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(ServerWebExchange exchange) {

        /***
         * Mono.fromRunnable()을 사용하여 비동기 작업을 정의하고, exchange.getSession()을 통해 세션을 가져와서 invalidate()를 호출하여 세션을 무효화합니다.
         * 세션 종료 처리를 doOnTerminate()를 통해 지정하고, 세션 무효화 작업이 끝날 때까지 기다리도록 합니다.
         */
        return Mono.fromRunnable(() -> {
            exchange.getSession().doOnTerminate(() -> {
                exchange.getSession().flatMap(webSession -> webSession.invalidate());
            });
        });


    }

}
