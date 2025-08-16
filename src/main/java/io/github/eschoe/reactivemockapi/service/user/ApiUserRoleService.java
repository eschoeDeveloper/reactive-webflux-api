package io.github.eschoe.reactivemockapi.service.user;

import reactor.core.publisher.Flux;

public interface ApiUserRoleService {
    Flux<String> getAhthoritiesByUsername(String username);
}
