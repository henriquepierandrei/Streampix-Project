package tech.pierandrei.StreamPix.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @param streamerName - Nome do streamer
 * @param minAmount - Mínimo para doar
 * @param maxNameLenght - Máximo de caractéres para o nome
 * @param maxMessageLenght - Máximo de caractéres para a mensagem
 * @param qrCodeUrl - URL para construir o QRCODE
 */
public record StreamerResponseDTO(
        @JsonProperty("streamer_name") String streamerName,
        @JsonProperty("min_amount") String minAmount,
        @JsonProperty("max_name_lenght") Integer maxNameLenght,
        @JsonProperty("max_message_lenght") Integer maxMessageLenght,
        @JsonProperty("qr_code_url") String qrCodeUrl
) {
}
