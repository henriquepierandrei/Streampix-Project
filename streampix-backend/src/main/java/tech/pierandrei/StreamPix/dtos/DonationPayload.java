package tech.pierandrei.StreamPix.dtos;

/**
 *
 * @param id - ID da transação do mercado pago
 * @param isDonated - Se foi doado com sucesso
 * @param payload - As informações da doação (Nome, Mensagem e Valor)
 */
public record DonationPayload(
        String id,
        Boolean isDonated,
        String audioUrl,
        Boolean qrCodeIsDarkTheme,
        Boolean addMessagesBellow,
        Boolean donateIsDarkTheme,
        ShortPayloadDTO payload) {}
