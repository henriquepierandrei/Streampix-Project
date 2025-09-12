package tech.pierandrei.StreamPix.websocket;

import com.sun.jdi.PrimitiveValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tech.pierandrei.StreamPix.dtos.DonationPayload;
import tech.pierandrei.StreamPix.dtos.GoalPayload;
import tech.pierandrei.StreamPix.dtos.PaymentPayload;
import tech.pierandrei.StreamPix.dtos.ShortPayloadDTO;
import tech.pierandrei.StreamPix.entities.StatusDonation;
import tech.pierandrei.StreamPix.entities.StreamerEntity;
import tech.pierandrei.StreamPix.exceptions.DonationNotFoundException;
import tech.pierandrei.StreamPix.exceptions.GoalsException;
import tech.pierandrei.StreamPix.exceptions.InvalidValuesException;
import tech.pierandrei.StreamPix.exceptions.StreamerNotFoundException;
import tech.pierandrei.StreamPix.repositories.GoalsRepository;
import tech.pierandrei.StreamPix.repositories.LogDonationsRepository;
import tech.pierandrei.StreamPix.repositories.StreamerRepository;
import tech.pierandrei.StreamPix.util.VariablesFormatted;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Controller
@RestController
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
    private final LogDonationsRepository logDonationsRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final StreamerRepository streamerRepository;
    private final VariablesFormatted variablesFormatted;
    private final GoalsRepository goalsRepository;
    public WebSocketController(LogDonationsRepository logDonationsRepository, SimpMessagingTemplate messagingTemplate, StreamerRepository streamerRepository,
                               VariablesFormatted variablesFormatted, GoalsRepository goalsRepository) {
        this.logDonationsRepository = logDonationsRepository;
        this.messagingTemplate = messagingTemplate;
        this.streamerRepository = streamerRepository;
        this.variablesFormatted = variablesFormatted;
        this.goalsRepository = goalsRepository;
    }



    // ============================================================================================================== //
    // Fila de doações
    private final Queue<DonationPayload> donationQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    // Fila de pagamentos específicos
    private final Queue<PaymentPayload> paymentQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService paymentExecutor = Executors.newSingleThreadScheduledExecutor();

    // Fila de Processamento da meta
    private final Queue<GoalPayload> goalQueue = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService goalExecutor = Executors.newSingleThreadScheduledExecutor();
    // ============================================================================================================== //



    // ============================================================================================================== //
    // Processa a fila a cada 500ms
    @PostConstruct
    public void init() {
        // fila antiga de doações
        executor.scheduleAtFixedRate(this::processQueue, 0, 500, TimeUnit.MILLISECONDS);

        // fila nova de pagamentos
        paymentExecutor.scheduleAtFixedRate(this::processPaymentQueue, 0, 500, TimeUnit.MILLISECONDS);

        // fila processamento da meta
        goalExecutor.scheduleAtFixedRate(this::processGoalQueue, 0, 500, TimeUnit.MILLISECONDS); // Precisa mudar o processPayment
    }

    // Processa a fila de forma segura para pagamentos
    private void processPaymentQueue() {
        PaymentPayload payload;
        while ((payload = paymentQueue.poll()) != null) {
            try {
                // envia apenas para o canal específico
                messagingTemplate.convertAndSend("/topics/payments/" + payload.transactionId(), payload);
            } catch (Exception e) {
                System.err.println("Erro enviando pagamento: " + e.getMessage());
            }
        }
    }

    // Processa a fila de forma segura
    private void processQueue() {
        DonationPayload payload;
        while ((payload = donationQueue.poll()) != null) {
            try {
                var user = streamerRepository.findById(1L)
                        .orElseThrow(() -> new InvalidValuesException("User is null"));
                if (user.getAutoPlay()) {
                    messagingTemplate.convertAndSend("/topics/donation", payload);
                }
            } catch (Exception e) {
                System.err.println("Erro enviando doação: " + e.getMessage());
            }
        }

    }

    // Processa a fila de meta segura
    private void processGoalQueue(){
        GoalPayload goalPayload;
        while ((goalPayload = goalQueue.poll()) != null) {
            try {
                // envia apenas para o canal específico
                messagingTemplate.convertAndSend("/topics/goal/", goalPayload);
            } catch (Exception e) {
                System.err.println("Erro enviando pagamento: " + e.getMessage());
            }
        }
    }
    // ============================================================================================================== //



    // ============================================================================================================== //
    // Adiciona a status do pagamento na fila
    public void notifyPayment(String transactionId, boolean isDonated, long timeRemainingSeconds) {
        PaymentPayload payload = new PaymentPayload(transactionId, isDonated, timeRemainingSeconds);
        paymentQueue.add(payload);
    }

    // Adiciona a doação na fila
    public void notifyDonationSuccess(String id, boolean isDonated, String audioUrl, ShortPayloadDTO dto) {
        var streamer = getStreamer();
        DonationPayload payload = new DonationPayload(id, isDonated, audioUrl, streamer.getQrCodeIsDarkTheme(), streamer.getAddMessagesBellow(), streamer.getDonateIsDarkTheme(), dto);
        donationQueue.add(payload);
    }


    // Adiciona a meta na fila
    public void notifyGoalIncrement(String uuid, BigDecimal finalBalance) {
        var goal = goalsRepository.findById(UUID.fromString(uuid))
                .orElseThrow(() -> new GoalsException("Meta não encontrada!"));

        goal.setCurrentBalance(finalBalance);
        this.goalsRepository.saveAndFlush(goal);

        // Calcula os dias restantes
        Instant now = Instant.now();
        Instant end = goal.getEndAt();
        int remainingDays = 0;

        if (end != null) {
            long diff = ChronoUnit.DAYS.between(now, end);
            remainingDays = (int) Math.max(diff, 0); // nunca negativo
        }

        // Cria payload com os dias restantes
        GoalPayload payload = new GoalPayload(
                String.valueOf(goal.getId()),
                goal.getCurrentBalance(),
                goal.getBalanceToAchieve(),
                goal.getReason(),
                remainingDays // agora é Integer
        );

        goalQueue.add(payload);
    }

    // ============================================================================================================== //
    private StreamerEntity getStreamer(){
        return this.streamerRepository.findById(1L).orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));
    }
    /**
     * Da play novamente na doação através do ID da doação
     * @param uuid - ID para buscar a doação
     */
    @PostMapping("/replay-donation")
    public void replayDonation(@RequestParam String uuid) {
        var streamer = getStreamer();
        var donation = this.logDonationsRepository.findByUuid(UUID.fromString(uuid)).orElseThrow(() -> new DonationNotFoundException("Doação não encontrada!"));
        if (donation.getStatusDonation().equals(StatusDonation.SUCCESSFUL_PAYMENT)){
            var payload = new DonationPayload(
                    donation.getTransactionId(),
                    true,
                    donation.getAudioUrl(),
                    streamer.getQrCodeIsDarkTheme(),
                    streamer.getAddMessagesBellow(),
                    streamer.getDonateIsDarkTheme(),
                    new ShortPayloadDTO(
                            donation.getName(),
                            donation.getMessage(),
                            variablesFormatted.formatDouble(donation.getAmount())
                    )
            );
            donationQueue.add(payload);
        };
    }
}