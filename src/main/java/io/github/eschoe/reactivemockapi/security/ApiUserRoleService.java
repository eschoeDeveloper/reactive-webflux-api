package io.github.eschoe.reactivemockapi.security;

import reactor.core.publisher.Flux;

public interface ApiUserRoleService {
    Flux<String> getAhthoritiesByUsername(String username);
}
