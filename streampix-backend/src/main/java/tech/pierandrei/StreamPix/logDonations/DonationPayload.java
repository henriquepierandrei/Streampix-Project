package tech.pierandrei.StreamPix.logDonations;

import com.fasterxml.jackson.annotation.JsonProperty;
import tech.pierandrei.StreamPix.dtos.ShortPayloadDTO;

/**
 *
 * @param id - ID da transação do mercado pago
 * @param isDonated - Se foi doado com sucesso
 * @param payload - As informações da doação (Nome, Mensagem e Valor)
 */
public record DonationPayload(
        String id,
        @JsonProperty("streamer-id") String streamerId,
        Boolean isDonated,
        String audioUrl,
        Boolean qrCodeIsDarkTheme,
        Boolean addMessagesBellow,
        Boolean donateIsDarkTheme,
        ShortPayloadDTO payload) {}
