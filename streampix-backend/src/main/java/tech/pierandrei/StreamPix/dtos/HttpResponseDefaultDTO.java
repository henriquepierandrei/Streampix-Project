package tech.pierandrei.StreamPix.dtos;

import org.springframework.http.HttpStatus;

public record HttpResponseDefaultDTO(
        HttpStatus status,
        String message
) {
}
