package tech.pierandrei.StreamPix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import tech.pierandrei.StreamPix.dtos.PayloadMercadoPagoDTO;
import java.util.UUID;

@Component
public class MercadoPagoApiConfig {

    @Value("${api.mercado.pago.access.token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String MERCADO_PAGO_URL  = "https://api.mercadopago.com/v1/payments";

    /**
     *
     Método responsável por fazer a requisição para a api gerar um qr code para pagamento!
     */
    public String requestToApi(PayloadMercadoPagoDTO payloadMercadoPagoDTO) {
        UUID uuid = UUID.randomUUID();

        /**
         * Configuração dos Headers
         */
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        /** Responsável por não duplicar transações*/
        headers.set("X-Idempotency-Key", uuid.toString());

        HttpEntity<?> entity = new HttpEntity<>(payloadMercadoPagoDTO, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(MERCADO_PAGO_URL, entity, String.class);

        return response.getBody();
    }
}