package tech.pierandrei.StreamPix.streamer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.pierandrei.StreamPix.dtos.HttpResponseDefaultDTO;
import tech.pierandrei.StreamPix.security.JwtUtil;
import tech.pierandrei.StreamPix.util.VariablesFormatted;

@Service
public class StreamerService {
    @Autowired
    private StreamerRepository streamerRepository;
    private InfoStreamerRepository infoStreamerRepository;
    private final VariablesFormatted variablesFormatted;
    private final JwtUtil jwtUtil;

    public StreamerService(InfoStreamerRepository infoStreamerRepository, VariablesFormatted variablesFormatted,
            JwtUtil jwtUtil) {
        this.infoStreamerRepository = infoStreamerRepository;
        this.variablesFormatted = variablesFormatted;
        this.jwtUtil = jwtUtil;
    }

    @Value("${url.frontend}")
    private String urlFrontend;

    /**
     * DTO para construir o dashboard
     * 
     * @param nickname
     * @param streamerBalance
     * @param isAutoPlay
     * @param minAmount
     * @param maxCharactersName
     * @param maxCharactersMessage
     * @param dto
     */

    /**
     * Obtém os dados do Streamer no Dashboard
     * 
     * @param token - Bearer Token
     * @return - Retornar os dados do Streamer no Dashboard
     */
    public StreamerDTO getStreamerInfo(String token) {
        var streamer = jwtUtil.getStreamerWithToken(token);
        var info = infoStreamerRepository.findByStreamerId(streamer.getId())
                .orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));

        // Criar o DTO interno separado
        InfoStreamerDTO infoDTO = new InfoStreamerDTO(
                info.getFullName(),
                info.getCpf(),
                info.getProfileImageUrl(),
                info.getLastAccess(),
                info.getDateOfRegistration(),
                info.getTotalDonationsReceived(),
                info.getTotalAmountReceived(),
                info.getReceiveNotification(),
                new HttpResponseDefaultDTO(
                        HttpStatus.OK,
                        "Dados do streamer obtido!"));

        return new StreamerDTO(
                streamer.getId(),
                streamer.getNickname(),
                variablesFormatted.formatDouble(streamer.getStreamerBalance()),
                streamer.getEmail(),
                streamer.getAutoPlay(),
                streamer.getMinAmount(),
                streamer.getMaxCharactersName(),
                streamer.getMaxCharactersMessage(),
                streamer.getQrCodeIsDarkTheme(),
                streamer.getAddMessagesBellow(),
                streamer.getDonateIsDarkTheme(),
                new HttpResponseDefaultDTO(
                        HttpStatus.OK,
                        "Dados do streamer obtido!"),
                infoDTO);
    }

    public StreamerDTO updateStreamerInfo(String token, StreamerDTO dto) {
        var streamer = jwtUtil.getStreamerWithToken(token);

        // Atualiza apenas se o campo não for nulo
        if (dto.nickname() != null && !dto.nickname().isBlank() && dto.nickname().length() <= 12) {
            streamer.setNickname(dto.nickname());
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

        if (dto.maxCharactersName() != null && dto.maxCharactersName() <= 12) {
            streamer.setMaxCharactersName(dto.maxCharactersName());
        }

        if (dto.maxCharactersMessage() != null && dto.maxCharactersMessage() <= 400) {
            streamer.setMaxCharactersMessage(dto.maxCharactersMessage());
        }

        if (dto.addMessagesBellow() != null) {
            streamer.setAddMessagesBellow(dto.addMessagesBellow());
        }

        if (dto.donateIsDarkTheme() != null) {
            streamer.setDonateIsDarkTheme(dto.donateIsDarkTheme());
        }

        if (dto.qrCodeIsDarkTheme() != null) {
            streamer.setQrCodeIsDarkTheme(dto.qrCodeIsDarkTheme());
        }

        // Salva no banco
        streamerRepository.save(streamer);

        // Cria InfoStreamerDTO vazio ou com dados básicos
        InfoStreamerDTO infoDTO = new InfoStreamerDTO(
                null, // fullName
                null, // cpf
                null, // profileImageUrl
                null, // lastAccess
                null, // dateOfRegistration
                0, // totalDonationsReceived
                0.0, // totalAmountReceived
                false, // receiveNotification
                new HttpResponseDefaultDTO(HttpStatus.OK, "Data not accessible here"));

        // Retorna DTO atualizado
        return new StreamerDTO(
                streamer.getId(),
                streamer.getNickname(),
                variablesFormatted.formatDouble(streamer.getStreamerBalance()),
                streamer.getEmail(),
                streamer.getAutoPlay(),
                streamer.getMinAmount(),
                streamer.getMaxCharactersName(),
                streamer.getMaxCharactersMessage(),
                streamer.getQrCodeIsDarkTheme(),
                streamer.getAddMessagesBellow(),
                streamer.getDonateIsDarkTheme(),
                new HttpResponseDefaultDTO(HttpStatus.OK, "Streamer atualizado com sucesso!"),
                infoDTO);
    }

    /**
     * Obtém os dados para doar de acordo com o nome do streamer.
     *
     * @param nickname
     * @return
     */
    public StreamerResponseDTO getStreamerByName(String nickname) {
        var streamer = this.streamerRepository.findByNickname(nickname)
                .orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));
        return new StreamerResponseDTO(
                streamer.getNickname(),
                String.valueOf(variablesFormatted.formatDouble(streamer.getMinAmount())),
                streamer.getMaxCharactersName(),
                streamer.getMaxCharactersMessage(),
                urlFrontend + "/" + streamer.getNickname());
    }

    public StreamerDTO getQrCodeTheme(String nickname) {
        var streamer = streamerRepository.findByNickname(nickname)
                .orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));

        // Cria InfoStreamerDTO vazio, sem dados sensíveis
        InfoStreamerDTO infoDTO = new InfoStreamerDTO(
                null, // fullName
                null, // cpf
                null, // profileImageUrl
                null, // lastAccess
                null, // dateOfRegistration
                0, // totalDonationsReceived
                0.0, // totalAmountReceived
                false, // receiveNotification
                new HttpResponseDefaultDTO(HttpStatus.OK, "Data not accessible here"));

        try {
            return new StreamerDTO(
                    streamer.getId(),
                    streamer.getNickname(),
                    "recycle", // placeholder balance
                    "recycle", // placeholder email
                    false, // autoplay
                    0.0, // minAmount
                    0, // maxCharactersName
                    0, // maxCharactersMessage
                    streamer.getQrCodeIsDarkTheme(),
                    streamer.getAddMessagesBellow(),
                    streamer.getDonateIsDarkTheme(),
                    new HttpResponseDefaultDTO(HttpStatus.OK, "Tema buscado com sucesso!"),
                    infoDTO);
        } catch (Exception e) {
            throw new StreamerNotFoundException("Tema não encontrado para o nickname informado!");
        }
    }

}
