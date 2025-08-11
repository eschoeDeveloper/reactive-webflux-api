package io.github.eschoe.reactivemockapi.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {

        MediaType ct = safeContentType(exchange.getRequest().getHeaders());

        // 헤더가 없거나 JSON이 아니면 스킵
        if (ct == null || MediaType.APPLICATION_JSON.isCompatibleWith(ct) == false) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다."));
        }

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.")))
                .flatMap(dataBuffer -> {

                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        String body = new String(bytes, StandardCharsets.UTF_8);

                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, String> map = objectMapper.readValue(body, new TypeReference<>() {});
                        String userid = map.get("userid");
                        String password = map.get("password");

                        if (userid == null || password == null) {
                            return Mono.error(new IllegalArgumentException("아이디 및 패스워드 누락"));
                        }

                        Authentication token =
                                UsernamePasswordAuthenticationToken.unauthenticated(userid, password);

                        return Mono.just(token);

                    } catch (Exception e) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청 본문", e));
                    } finally {
                        DataBufferUtils.release(dataBuffer); // 꼭 release!
                    }
                });

    }

    private MediaType safeContentType(HttpHeaders headers) {
        MediaType ct = headers.getContentType();      // 보통 null일 수도 있음
        if (ct != null) return ct;
        String raw = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        if (raw == null) return null;
        try {
            return MediaType.parseMediaType(raw);     // 잘못된 값이면 예외 → null 처리
        } catch (InvalidMediaTypeException e) {
            return null;
        }
    }

}
