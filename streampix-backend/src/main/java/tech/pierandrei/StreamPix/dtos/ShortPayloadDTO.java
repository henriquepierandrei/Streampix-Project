package tech.pierandrei.StreamPix.dtos;


/**
 *
 * @param name - Nome do doador
 * @param message - Mensagem para o streamer
 * @param amount - Valor do Donate
 */
public record ShortPayloadDTO(
        String name,
        String message,
        String amount
) {

}
