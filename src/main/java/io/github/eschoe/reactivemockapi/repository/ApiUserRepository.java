package io.github.eschoe.reactivemockapi.repository;

import io.github.eschoe.reactivemockapi.dto.auth.ApiUser;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ApiUserRepository extends ReactiveCrudRepository<ApiUser, String> {

    @Query("SELECT u.USERID as userid, u.USERNAME as username, u.PASSWORD as password FROM PUBLIC.API_USER u WHERE u.USERID = :userid")
    Mono<ApiUser> findByUserid(String userid);
    Mono<ApiUser> findByUsername(String username);
}
