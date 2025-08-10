package io.github.eschoe.reactivemockapi.repository;

import io.github.eschoe.reactivemockapi.dto.ApiUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

public interface ApiUserRepository extends ReactiveCrudRepository<ApiUser, String> {
    Mono<ApiUser> findByUsername(String username);
}
