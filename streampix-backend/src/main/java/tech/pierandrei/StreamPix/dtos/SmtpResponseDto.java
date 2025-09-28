package tech.pierandrei.StreamPix.dtos;

import org.springframework.http.HttpStatus;
public record SmtpResponseDto (
    HttpStatus httpStatus,
    String message,
    Boolean sentEmail,
    String sessionToken
){}
