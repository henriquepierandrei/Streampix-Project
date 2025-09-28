package tech.pierandrei.StreamPix.dtos;

import org.springframework.http.HttpStatus;

public record RegisterResponseDto (
    HttpStatus httpStatus,
    String message,
    boolean sentEmail
){}
