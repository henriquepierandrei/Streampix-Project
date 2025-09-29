package tech.pierandrei.StreamPix.streamer;

public class StreamerUnauthorizedException extends RuntimeException{
    public StreamerUnauthorizedException(String message) {
        super(message);
    }
}
