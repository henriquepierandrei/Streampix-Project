package tech.pierandrei.StreamPix.streamer;

/**
 * Exceção para retornar um erro na busca do streamer
 */
public class StreamerNotFoundException extends RuntimeException{
    public StreamerNotFoundException(String message) {
        super(message);
    }
}
