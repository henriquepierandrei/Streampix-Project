package tech.pierandrei.StreamPix.exceptions.streamerExceptions;

public class StreamerUnauthorizedException extends RuntimeException{
    public StreamerUnauthorizedException(String message) {
        super(message);
    }
}
