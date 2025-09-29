package tech.pierandrei.StreamPix.security.dtos;

import org.springframework.http.HttpStatus;

public record RegisterResponseDTO(
    HttpStatus httpStatus,
    String message,
    boolean sentEmail
){}
