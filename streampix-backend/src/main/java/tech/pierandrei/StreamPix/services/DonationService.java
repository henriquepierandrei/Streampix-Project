package tech.pierandrei.StreamPix.services;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.pierandrei.StreamPix.config.MercadoPagoApiConfig;
import tech.pierandrei.StreamPix.dtos.DonationFullRequestDto;
import tech.pierandrei.StreamPix.dtos.PayloadMercadoPagoDTO;
import tech.pierandrei.StreamPix.dtos.ShortResponseApiDTO;
import tech.pierandrei.StreamPix.entities.LogDonationsEntity;
import tech.pierandrei.StreamPix.entities.StatusDonation;
import tech.pierandrei.StreamPix.exceptions.DonationNotFoundException;
import tech.pierandrei.StreamPix.exceptions.InvalidValuesException;
import tech.pierandrei.StreamPix.exceptions.PaymentExpiredException;
import tech.pierandrei.StreamPix.repositories.LogDonationsRepository;
import tech.pierandrei.StreamPix.repositories.StreamerRepository;
import tech.pierandrei.StreamPix.util.VariablesFormatted;
import tech.pierandrei.StreamPix.websocket.WebSocketController;
import java.time.*;
import java.util.UUID;

@Service
public class DonationService {
    private static final Logger log = LoggerFactory.getLogger(DonationService.class);
    private final StreamerRepository streamerRepository;
    private final MercadoPagoApiConfig mercadoPagoApiConfig;
    private final LogDonationsRepository logDonationsRepository;
    private final AudioService audioService;
    private final VariablesFormatted variablesFormatted;

    // Response para retornar o qrcode para pagar no frontend
    public record DonationInfoDTO(
            @JsonProperty("already_paid") Boolean alreadyPaid,
            @JsonProperty("time_remaining_seconds") long timeRemainingInSeconds,
            String qrcode,
            String amount,
            String name

    ){}

    public DonationService(StreamerRepository streamerRepository, MercadoPagoApiConfig mercadoPagoApiConfig, LogDonationsRepository logDonationsRepository, AudioService audioService, VariablesFormatted variablesFormatted) {
        this.streamerRepository = streamerRepository;
        this.mercadoPagoApiConfig = mercadoPagoApiConfig;
        this.logDonationsRepository = logDonationsRepository;
        this.audioService = audioService;
        this.variablesFormatted = variablesFormatted;
    }


    @Autowired
    private WebSocketController webSocketController;

    /**
     * Valida os dados
     * @param amount - Valor doado
     * @param message - Mensagem da doação
     * @param name - Nome do doador
     * @return true se todos os dados estiverem adequados
     */
    private Long validateAllValuesAndGetStreamerId(Double amount, String message, String name, String streamerName) {
        var user = this.streamerRepository.findByStreamerName(streamerName).orElseThrow(() -> new InvalidValuesException("User not found"));

        // Formatação do valor mínimo
        String amountFormatted = String.format("%.2f", user.getMinAmount()).replace(".", ",");

        // Validações usando a formatação correta
        if (amount < user.getMinAmount()) {
            throw new InvalidValuesException("O valor deve ser maior que R$" + amountFormatted + ".");
        }
        if (message != null && message.length() > user.getMaxCharactersMessage()) {
            throw new InvalidValuesException("A mensagem deve conter de 0 a " + user.getMaxCharactersMessage() + " caracteres.");
        }
        if (name != null && name.length() > user.getMaxCharactersName()) {
            throw new InvalidValuesException("O nome deve conter de 0 a " + user.getMaxCharactersName() + " caracteres.");
        }
        return user.getId();
    }

    /**
     * Armazena a doação no banco de dados
     * @return
     * @throws Exception
     */
    public ShortResponseApiDTO donationService(DonationFullRequestDto dto, String streamerName) throws Exception {
        // Valida todos os dados antes
        var streamerId = validateAllValuesAndGetStreamerId(Double.valueOf(dto.amount()), dto.message(), dto.name(), streamerName);

        // Agora você passa essa string no payload
        PayloadMercadoPagoDTO payloadMercadoPagoDTO = new PayloadMercadoPagoDTO(
                Double.valueOf(dto.amount()),
                "StreamPix - Donation",
                "pix",
                new PayloadMercadoPagoDTO.Payer(
                        "donation@streampix.com",
                        dto.name(),
                        null
                )

        );
        // Obtem o response recebido da API do mercado pago
        var response = this.mercadoPagoApiConfig.requestToApi(payloadMercadoPagoDTO);

        // Variável responsável por receber o json e mapear para retornar no método
        ShortResponseApiDTO dtoResponse = new ShortResponseApiDTO();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();
            dtoResponse = objectMapper.readValue(response, ShortResponseApiDTO.class);

        }catch (Exception e){
            log.error("Mapper got error, see the reason:" + e.getMessage());
        }

        // Gerar ID para tornar único essa transação
        UUID ownId = UUID.randomUUID();
        dtoResponse.setTransactionStreamPixId(ownId);
        dtoResponse.setQrCodeToHtmlImage("data:image/png;base64," + dtoResponse.getPointOfInteraction().getTransactionData().getQrCodeBase64());


        String audioUrl;
        try {
            log.debug(">" + dto);

            audioUrl = audioService.generateAudio(ownId, dto);
        }catch (Exception e){
            log.error("Log error: " + e.getMessage());
            throw new Exception(e.getMessage());
        }

        try {
            var newLog = new LogDonationsEntity(
                    ownId,
                    dtoResponse.getTransactionAmount(),
                    dto.name(),
                    dto.message(),
                    audioUrl,
                    Instant.now(),
                    dtoResponse.getId(),
                    StatusDonation.PENDING_PAYMENT,
                    dtoResponse.getPointOfInteraction().getTransactionData().getQrCode()
            );
            // Adiciona o id do streamer para referenciar
            newLog.setStreamerId(streamerId);
            this.logDonationsRepository.save(newLog);
            log.debug("Log registered: [ ID ]: " + newLog.getTransactionId());
        }catch (Exception e){
            log.error("Log error: " + e.getMessage());
            throw new Exception(e.getMessage());
        }
        return dtoResponse;
    }

    /**
     * Obtém as informações para construir a página para enviar o pagamento
     * @param id
     * @return
     */
    public DonationInfoDTO getDonationInfoToPay(String id) {
        UUID idToUuid = UUID.fromString(id);
        var donation = this.logDonationsRepository.findByUuid(idToUuid)
                .orElseThrow(() -> new DonationNotFoundException("Nenhuma doação encontrada!"));

        boolean paid = false;

        // Tempo máximo de vida: 5 minutos
        Instant expiresAt = donation.getDonatedAt().plus(Duration.ofMinutes(5));
        Instant now = Instant.now();

        // Verifica se já expirou
        if (now.isAfter(expiresAt) && !donation.getStatusDonation().equals(StatusDonation.SUCCESSFUL_PAYMENT)) {
            throw new PaymentExpiredException("Pagamento foi expirado, crie um novo QrCode!");
        }

        // Calcula segundos restantes
        long timeRemainingInSeconds = Duration.between(now, expiresAt).getSeconds();
        if (timeRemainingInSeconds < 0) {
            timeRemainingInSeconds = 0; // segurança para não retornar número negativo
        }

        if (donation.getStatusDonation().equals(StatusDonation.SUCCESSFUL_PAYMENT)) {
            paid = true;
        }

        this.webSocketController.notifyPayment(id, paid, timeRemainingInSeconds);
        return new DonationInfoDTO(
                paid,
                timeRemainingInSeconds,
                donation.getQrCode(),
                String.valueOf(variablesFormatted.formatDouble(donation.getAmount())),
                donation.getName()
        );
    }
}
