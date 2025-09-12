package tech.pierandrei.StreamPix.exceptions;

public class JwtInvalidException extends RuntimeException{
    public JwtInvalidException(String message) {
        super(message);
    }
}
