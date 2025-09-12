package tech.pierandrei.StreamPix.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.pierandrei.StreamPix.dtos.DonationPayload;
import tech.pierandrei.StreamPix.dtos.ShortPayloadDTO;
import tech.pierandrei.StreamPix.entities.StatusDonation;
import tech.pierandrei.StreamPix.exceptions.GoalsException;
import tech.pierandrei.StreamPix.exceptions.InvalidValuesException;
import tech.pierandrei.StreamPix.exceptions.StreamerNotFoundException;
import tech.pierandrei.StreamPix.repositories.GoalsRepository;
import tech.pierandrei.StreamPix.repositories.LogDonationsRepository;
import tech.pierandrei.StreamPix.repositories.StreamerRepository;
import tech.pierandrei.StreamPix.util.VariablesFormatted;
import tech.pierandrei.StreamPix.websocket.WebSocketController;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

@Service
@Transactional
public class WebhookService {
    private static final Logger log = LoggerFactory.getLogger(WebhookService.class);
    private final LogDonationsRepository logDonationsRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final WebSocketController webSocketController;
    private final StreamerRepository streamerRepository;
    private final VariablesFormatted variablesFormatted;
    private final GoalsRepository goalsRepository;

    public WebhookService(LogDonationsRepository logDonationsRepository, WebSocketController webSocketController, StreamerRepository streamerRepository, VariablesFormatted variablesFormatted, GoalsRepository goalsRepository) {
        this.logDonationsRepository = logDonationsRepository;
        this.webSocketController = webSocketController;
        this.streamerRepository = streamerRepository;
        this.variablesFormatted = variablesFormatted;
        this.goalsRepository = goalsRepository;
    }

    @Value("${api.mercado.pago.access.token}")
    private String accessToken;

    /**
     *
     * @param id - Id da transação
     */
    public void verifyStatusDetail(String id) {
        try {
            String url = "https://api.mercadopago.com/v1/payments/" + id;

            // Token de acesso para verificar o status do pagamento ( Concluído / Não Concluído )
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // Extraindo o campo do Json que retorna o status da transação
            if (response.getStatusCode() == HttpStatus.OK) {
                String body = response.getBody();
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(body);
                String statusDetail = root.path("status_detail").asText();
                System.out.println("Status detail: " + statusDetail);
                // Atualiza o Status do LOG dentro do DB
                updateDonationStatus(id, statusDetail);
            } else {
                log.debug("Erro ao buscar status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param id - Id da transação
     * @param statusDetail - Status recebido da API do mercado Pago
     */
    public void updateDonationStatus(String id, String statusDetail){
        // Busca a log para atualizar o status
        var log = this.logDonationsRepository.findByTransactionId(id)
                .orElseThrow(() -> new InvalidValuesException("Log not found with id: " + id));

        StatusDonation status = null;
        if (statusDetail == null || statusDetail.isEmpty() || statusDetail.isBlank()) throw new InvalidValuesException("status can't be null");

        // Atualiza o status de acordo com o status retornado do mercado pago
        if (statusDetail.equals("pending_waiting_transfer")) status = StatusDonation.PENDING_PAYMENT;
        if (statusDetail.equals("accredited")) status = StatusDonation.SUCCESSFUL_PAYMENT;


        if (status != null) log.setStatusDonation(status);
        this.logDonationsRepository.saveAndFlush(log);

        var response = new ShortPayloadDTO(
                log.getName(),
                log.getMessage(),
                variablesFormatted.formatDouble(log.getAmount())
        );

        // Se o pagamento for concluído
        if (log.getStatusDonation().equals(StatusDonation.SUCCESSFUL_PAYMENT)) {
            // Busca o streamer para verificar os principais dados antes de processar o envio da doação
            var user = this.streamerRepository.findById(1L).orElseThrow(() -> new InvalidValuesException("User is null"));

            // Atualiza o saldo do streamer
            user.setStreamerBalance(user.getStreamerBalance() + log.getAmount());
            this.streamerRepository.saveAndFlush(user);


            // Calcular tempo restante
            Instant expiresAt = log.getDonatedAt().plus(Duration.ofMinutes(5));
            Instant now = Instant.now();
            // Calcula segundos restantes
            long timeRemainingInSeconds = Duration.between(now, expiresAt).getSeconds();
            if (timeRemainingInSeconds < 0) {
                timeRemainingInSeconds = 0; // segurança para não retornar número negativo
            }

            // Notifica o pagamento
            webSocketController.notifyPayment(String.valueOf(log.getUuid()), true, timeRemainingInSeconds);

            // Envia a doação para o frontend processar e exibir, através do WebSocket
            webSocketController.notifyDonationSuccess(log.getTransactionId(),true, log.getAudioUrl(), response);

            // Saldo do streamer
            var balance = BigDecimal.valueOf(user.getStreamerBalance());

            // Incrementa na meta
            var goal = this.goalsRepository.findByUserId(1L);
            if (goal.isPresent()) webSocketController.notifyGoalIncrement(String.valueOf(goal.get().getId()), balance.add(BigDecimal.valueOf(log.getAmount())));

        };
        if (log.getStatusDonation().equals(StatusDonation.UNSUCCESSFUL_PAYMENT)) webSocketController.notifyDonationSuccess(log.getTransactionId(), false, log.getAudioUrl(),  response);
    }

}