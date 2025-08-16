package io.github.eschoe.reactivemockapi.security;

import io.github.eschoe.reactivemockapi.dto.auth.ApiUser;
import io.github.eschoe.reactivemockapi.repository.ApiUserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiUserDetailsService implements ReactiveUserDetailsService {

    private final ApiUserRepository apiUserRepository;
    private final ApiUserRoleService apiUserRoleService;

    public ApiUserDetailsService(ApiUserRepository apiUserRepository, ApiUserRoleService apiUserRoleService) {
        this.apiUserRepository = apiUserRepository;
        this.apiUserRoleService = apiUserRoleService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String userid) {

        System.out.println(userid);

        return apiUserRepository.findByUserid(userid)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("사용자를 찾을 수 없습니다.")))
                .flatMap(u ->
                    apiUserRoleService.getAhthoritiesByUsername(userid)
                            .defaultIfEmpty("ROLE_USER")
                            .distinct()
                            .collectList()
                            .map(roles -> toUserDetails(u, roles))
                );

    }

    private UserDetails toUserDetails(ApiUser apiUser, List<String> roles) {

        System.out.println("toUserDetails: " + apiUser);

        System.out.println("userid: " + apiUser.getUserid());
        System.out.println("username: " + apiUser.getUsername());
        System.out.println("password: " + apiUser.getPassword());

        List<GrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());

        return User.withUsername(apiUser.getUserid())
                .password(apiUser.getPassword())
                .authorities(authorities)
                .accountExpired(true)
                .accountLocked(true)
                .credentialsExpired(false)
                .disabled(false)
                .build();

    }


}
