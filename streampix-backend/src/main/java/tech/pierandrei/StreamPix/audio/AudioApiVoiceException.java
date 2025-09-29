package tech.pierandrei.StreamPix.audio;

/**
 * Exceção para retornar erro na API criada para gerar a voz
 */
public class AudioApiVoiceException extends RuntimeException{
    public AudioApiVoiceException(String message) {
        super(message);
    }
}
