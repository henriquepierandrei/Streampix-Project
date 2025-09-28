package tech.pierandrei.StreamPix.exceptions.streamerExceptions;

/**
 * Exceção para retornar um erro na busca do streamer
 */
public class StreamerNotFoundException extends RuntimeException{
    public StreamerNotFoundException(String message) {
        super(message);
    }
}
