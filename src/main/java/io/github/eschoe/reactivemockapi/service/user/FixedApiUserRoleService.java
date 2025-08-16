package io.github.eschoe.reactivemockapi.service.user;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class FixedApiUserRoleService implements ApiUserRoleService {

    /**
     * 권한 테이블 추가시 확장성 고려함
     * */
    @Override
    public Flux<String> getAhthoritiesByUsername(String username) {
        return Flux.just("ROLE_USER");
    }
    
}
