package io.github.eschoe.reactivemockapi.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
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
        if (ct == null || !MediaType.APPLICATION_JSON.isCompatibleWith(ct)) {
            return Mono.empty();
        }

        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    try {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        String body = new String(bytes, StandardCharsets.UTF_8);

                        ObjectMapper objectMapper = new ObjectMapper();
                        Map<String, String> map = objectMapper.readValue(body, new TypeReference<>() {});
                        String username = map.get("username");
                        String password = map.get("password");

                        if (username == null || password == null) {
                            return Mono.error(new IllegalArgumentException("username/password 누락"));
                        }

                        // 인증 전 토큰
                        Authentication token =
                                UsernamePasswordAuthenticationToken.unauthenticated(username, password);

                        return Mono.just(token);
                    } catch (Exception e) {
                        return Mono.error(e);
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
