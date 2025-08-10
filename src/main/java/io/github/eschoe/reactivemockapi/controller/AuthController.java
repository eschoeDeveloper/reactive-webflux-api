package io.github.eschoe.reactivemockapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public Mono<Void> login(String username, String password) {

        return Mono.empty();

    }

    @PostMapping("/logout")
    public Mono<Void> logout(ServerWebExchange exchange) {

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
