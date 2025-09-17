package tech.pierandrei.StreamPix.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.pierandrei.StreamPix.services.WebhookService;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/webhook")
public class WebhookController {
    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);
    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    /**
     * Webhook que recebe o status do pagamento da doação
     * @param payload - Payload recebido da API do Mercado Pago
     * @return - Envia o payload para o websocket enviar a mensagem pro OBS
     */
    @PostMapping("/mercadopago")
    public ResponseEntity<Void> receive(@RequestBody Map<String, Object> payload) {
        log.debug("Webhook recebido:" + payload);

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) payload.get("data");

    
        var id = data.get("id").toString(); // Esse é o ID do pagamento
        // Processa o webhook com delay de forma assíncrona
        CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS)
                .execute(() -> {
                    try {
                        webhookService.verifyStatusDetail(id);
                    } catch (Exception e) {
                        log.error("Erro ao processar webhook com delay para ID: " + id, e);
                    }
                });

        // Retorna resposta imediatamente para o MercadoPago
        return ResponseEntity.ok().build();
    }

}
