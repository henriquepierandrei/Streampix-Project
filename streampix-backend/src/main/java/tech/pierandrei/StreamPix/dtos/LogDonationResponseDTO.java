package tech.pierandrei.StreamPix.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.UUID;

/**
 *
 * @param uuid - Id da doação
 * @param name - Nome do Doador
 * @param message - Mensagem da doação
 * @param amount - Valor doado
 * @param donatedAt - Data da doação
 * @param audioUrl - Áudio gerado pela IA extraído da mensagem
 */
public record LogDonationResponseDTO(
        UUID uuid,
        String name,
        String message,
        String amount,
        @JsonProperty("donated_at") Instant donatedAt,
        @JsonProperty("audio_url") String audioUrl
) {
}
