package tech.pierandrei.StreamPix.security;

public class JwtInvalidException extends RuntimeException{
    public JwtInvalidException(String message) {
        super(message);
    }
}
