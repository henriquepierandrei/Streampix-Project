package tech.pierandrei.StreamPix.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @param id - ID da transação do mercado pago
 * @param isDonated - Se foi doado com sucesso
 * @param payload - As informações da doação (Nome, Mensagem e Valor)
 */
public record DonationPayload(
        String id,
        @JsonProperty("streamer-id") Long streamerId,
        Boolean isDonated,
        String audioUrl,
        Boolean qrCodeIsDarkTheme,
        Boolean addMessagesBellow,
        Boolean donateIsDarkTheme,
        ShortPayloadDTO payload) {}
