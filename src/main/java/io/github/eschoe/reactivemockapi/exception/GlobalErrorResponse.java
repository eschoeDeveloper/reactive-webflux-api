package io.github.eschoe.reactivemockapi.exception;

public record GlobalErrorResponse(String code, String message, String path) {}
