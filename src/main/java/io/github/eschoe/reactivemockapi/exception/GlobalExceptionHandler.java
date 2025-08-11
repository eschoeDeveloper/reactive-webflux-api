package io.github.eschoe.reactivemockapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<GlobalErrorResponse> handleBind(WebExchangeBindException ex, ServerHttpRequest req) {
        String msg = ex.getFieldErrors().stream()
                .map(fe -> fe.getField() + " " + fe.getDefaultMessage())
                .findFirst().orElse(ex.getMessage());

        return ResponseEntity.badRequest()
                .body(new GlobalErrorResponse("VALIDATION_ERROR", msg, req.getPath().value()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalErrorResponse> handleIllegal(IllegalArgumentException ex, ServerHttpRequest req) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new GlobalErrorResponse("BAD_REQUEST", ex.getMessage(), req.getPath().value()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleRse(ResponseStatusException ex, ServerHttpRequest req) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", java.time.Instant.now().toString());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ex.getStatusCode().toString());
        body.put("message", ex.getReason());
        body.put("path", req.getPath().value());
        return ResponseEntity.status(ex.getStatusCode())
                .body(body);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<GlobalErrorResponse> handleUnknown(Throwable ex, ServerHttpRequest req) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new GlobalErrorResponse("INTERNAL_ERROR", "Unexpected error", req.getPath().value()));
    }

}
