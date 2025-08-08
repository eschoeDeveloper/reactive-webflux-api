package io.github.eschoe.reactivemockapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/login")
    public Mono<Void> login(String username, String password) {

        return Mono.empty();

    }

}
