package tech.pierandrei.StreamPix.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.pierandrei.StreamPix.dtos.HttpResponseDefaultDTO;
import tech.pierandrei.StreamPix.dtos.StreamerResponseDTO;
import tech.pierandrei.StreamPix.exceptions.InvalidValuesException;
import tech.pierandrei.StreamPix.exceptions.StreamerNotFoundException;
import tech.pierandrei.StreamPix.repositories.StreamerRepository;
import tech.pierandrei.StreamPix.security.JwtUtil;
import tech.pierandrei.StreamPix.util.VariablesFormatted;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
public class StreamerService {
    @Autowired
    private StreamerRepository streamerRepository;
    private final VariablesFormatted variablesFormatted;
    private final JwtUtil jwtUtil;

    public StreamerService(VariablesFormatted variablesFormatted, JwtUtil jwtUtil) {
        this.variablesFormatted = variablesFormatted;
        this.jwtUtil = jwtUtil;
    }

    @Value("${url.frontend}")
    private String urlFrontend;


    /**
     * DTO para construir o dashboard
     * @param streamerName
     * @param streamerBalance
     * @param isAutoPlay
     * @param minAmount
     * @param maxCharactersName
     * @param maxCharactersMessage
     * @param dto
     */
    public record StreamerDTO(
            @JsonProperty("streamer_name") String streamerName,
            @JsonProperty("streamer_balance") String streamerBalance,
            @JsonProperty("is_auto_play") Boolean isAutoPlay,
            @JsonProperty("min_amount") Double minAmount,
            @JsonProperty("max_characters_name") Integer maxCharactersName,
            @JsonProperty("max_characters_message") Integer maxCharactersMessage,
            @JsonProperty("qr_code_is_dark_theme")Boolean qrCodeIsDarkTheme,
            @JsonProperty("add_messages_bellow")Boolean addMessagesBellow,
            @JsonProperty("donate_is_dark_theme")Boolean donateIsDarkTheme,
            @JsonProperty("http_response")  HttpResponseDefaultDTO dto

    ){
        public StreamerDTO {
        }
    }

    /**
     * Obtém os dados do Streamer no Dashboard
     * @param token - Bearer Token
     * @return - Retornar os dados do Streamer no Dashboard
     */
    public StreamerDTO getStreamerInfo(String token){
        var streamer = jwtUtil.getStreamerWithToken(token);

        DecimalFormat df = new DecimalFormat("#0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

        return new StreamerDTO(
                streamer.getStreamerName(),
                variablesFormatted.formatDouble(streamer.getStreamerBalance()),
                streamer.getAutoPlay(),
                streamer.getMinAmount(),
                streamer.getMaxCharactersName(),
                streamer.getMaxCharactersMessage(),
                streamer.getQrCodeIsDarkTheme(),
                streamer.getAddMessagesBellow(),
                streamer.getDonateIsDarkTheme(),
                new HttpResponseDefaultDTO(
                        HttpStatus.OK,
                        "Dados do streamer obtido!"
                )
        );

    }

    /**
     * Atualiza o Streamer
     * @param dto - Payload
     * @return - Retornar o token
     */
    public StreamerDTO updateStreamerInfo(String token, StreamerDTO dto) {
        var streamer = jwtUtil.getStreamerWithToken(token);

        // Atualiza apenas se o campo não for nulo
        if (dto.streamerName() != null && !dto.streamerName().isBlank() && dto.streamerName.length() <= 12) {
            streamer.setStreamerName(dto.streamerName());
        }

        if (dto.streamerBalance() != null) {
            String balance = dto.streamerBalance().replace(",", ".");
            streamer.setStreamerBalance(Double.valueOf(balance));
        }


        if (dto.isAutoPlay() != null) {
            streamer.setAutoPlay(dto.isAutoPlay());
        }

        if (dto.minAmount() != null) {
            streamer.setMinAmount(dto.minAmount());
        }

        if (dto.maxCharactersName() != null && dto.maxCharactersName <= 12) {
            streamer.setMaxCharactersName(dto.maxCharactersName());
        }

        if (dto.maxCharactersMessage() != null && dto.maxCharactersName <= 400) {
            streamer.setMaxCharactersMessage(dto.maxCharactersMessage());
        }

        if (dto.addMessagesBellow() != null){
            streamer.setAddMessagesBellow(dto.addMessagesBellow());
        }

        if (dto.donateIsDarkTheme != null){
            streamer.setDonateIsDarkTheme(dto.donateIsDarkTheme);
        }

        if (dto.qrCodeIsDarkTheme != null){
            streamer.setQrCodeIsDarkTheme(dto.qrCodeIsDarkTheme);
        }

        // Salva no banco
        streamerRepository.save(streamer);

        // Retorna DTO atualizado
        return new StreamerDTO(
                streamer.getStreamerName(),
                variablesFormatted.formatDouble(streamer.getStreamerBalance()),
                streamer.getAutoPlay(),
                streamer.getMinAmount(),
                streamer.getMaxCharactersName(),
                streamer.getMaxCharactersMessage(),
                streamer.getQrCodeIsDarkTheme(),
                streamer.getAddMessagesBellow(),
                streamer.getDonateIsDarkTheme(),
                new HttpResponseDefaultDTO(HttpStatus.OK, "Streamer atualizado com sucesso!")
        );
    }

    /** Obtém os dados para doar de acordo com o nome do streamer.
     *
     * @param streamerName
     * @return
     */
    public StreamerResponseDTO getStreamerByName(String streamerName){
        var streamer = this.streamerRepository.findByStreamerName(streamerName).orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));
        return new StreamerResponseDTO(
                streamer.getStreamerName(),
                String.valueOf(variablesFormatted.formatDouble(streamer.getMinAmount())),
                streamer.getMaxCharactersName(),
                streamer.getMaxCharactersMessage(),
                urlFrontend + "/" + streamer.getStreamerName()
        );
    }


    public StreamerDTO getQrCodeTheme(String streamerName){
        var streamer = streamerRepository.findByStreamerName(streamerName).orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));
        try {
            return new StreamerDTO(
                    streamer.getStreamerName(),
                    "recycle",
                    false,
                    0.0,
                    0,
                    0,
                    streamer.getQrCodeIsDarkTheme(),
                    streamer.getAddMessagesBellow(),
                    streamer.getDonateIsDarkTheme(),
                    new HttpResponseDefaultDTO(HttpStatus.OK, "Tema buscado com sucesso!")
            );
        }catch (Exception e){
            // Caso não seja igual, pode retornar null ou lançar exceção
            throw new StreamerNotFoundException("Tema não encontrado para o UUID informado!");
        }
    }
}
