package tech.pierandrei.StreamPix.services;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import tech.pierandrei.StreamPix.dtos.EmailValidationResponseDTO;
import tech.pierandrei.StreamPix.dtos.RegisterResponseDto;
import tech.pierandrei.StreamPix.dtos.SmtpResponseDto;
import tech.pierandrei.StreamPix.entities.InfoStreamerEntity;
import tech.pierandrei.StreamPix.entities.StreamerEntity;
import tech.pierandrei.StreamPix.exceptions.EmailSendException;
import tech.pierandrei.StreamPix.exceptions.InvalidCredentialsException;
import tech.pierandrei.StreamPix.exceptions.JwtInvalidException;
import tech.pierandrei.StreamPix.exceptions.streamerExceptions.StreamerNotFoundException;
import tech.pierandrei.StreamPix.repositories.InfoStreamerRepository;
import tech.pierandrei.StreamPix.repositories.StreamerRepository;
import tech.pierandrei.StreamPix.security.JwtUtil;
import tech.pierandrei.StreamPix.security.StreamerDetails;
import tech.pierandrei.StreamPix.security.dtos.AuthRequestDto;
import tech.pierandrei.StreamPix.security.dtos.AuthResponseDto;
import tech.pierandrei.StreamPix.security.dtos.RegisterRequestDto;
import tech.pierandrei.StreamPix.util.EmailValidationClient;

@Service
@Transactional
public class AuthService {
    private final StreamerRepository repository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final InfoStreamerRepository infoStreamerRepository;
    private final EmailValidationClient emailValidationClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthService(StreamerRepository repository, AuthenticationManager authenticationManager, JwtUtil jwtUtil,
            InfoStreamerRepository infoStreamerRepository, EmailValidationClient emailValidationClient) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.infoStreamerRepository = infoStreamerRepository;
        this.emailValidationClient = emailValidationClient;
    }

    private boolean isValidCPF(String cpf) {
        if (cpf == null)
            return false;

        // Remove qualquer caractere que não seja número
        cpf = cpf.replaceAll("\\D", "");

        // Tem que ter 11 dígitos
        if (cpf.length() != 11)
            return false;

        // Rejeita CPFs com todos dígitos iguais (ex: 00000000000, 11111111111)
        if (cpf.matches("(\\d)\\1{10}"))
            return false;

        try {
            // Cálculo do primeiro dígito verificador
            int sum = 0;
            for (int i = 0; i < 9; i++) {
                sum += (cpf.charAt(i) - '0') * (10 - i);
            }
            int firstDigit = 11 - (sum % 11);
            if (firstDigit >= 10)
                firstDigit = 0;

            // Cálculo do segundo dígito verificador
            sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += (cpf.charAt(i) - '0') * (11 - i);
            }
            int secondDigit = 11 - (sum % 11);
            if (secondDigit >= 10)
                secondDigit = 0;

            // Confere com os dígitos informados
            return (firstDigit == (cpf.charAt(9) - '0')) &&
                    (secondDigit == (cpf.charAt(10) - '0'));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Valida as credenciais enviadas no paylaod
     * 
     * @param dto - Payload para verificar
     */
    private boolean validateCredentials(RegisterRequestDto dto) {
        // Verifica se o CPF existe
        var infoOpt = infoStreamerRepository.findByCpf(dto.cpf());

        if (dto.email() == null || repository.existsByEmailAndIsAccountValid(dto.email(), true))
            throw new InvalidCredentialsException("Esse email já existe!");

        if (dto.nickname() == null || repository.existsByNicknameAndIsAccountValid(dto.nickname().toLowerCase(), true))
            throw new InvalidCredentialsException("Esse nome já existe!");
        if (dto.cpf() == null || !isValidCPF(dto.cpf()))
            throw new InvalidCredentialsException("CPF inválido!");

        // Se info existir, verifica se a conta do streamer também é válida
        if (infoOpt.isPresent()) {
            var streamerOpt = repository.findById(infoOpt.get().getStreamerId());
            if (streamerOpt.isPresent() && streamerOpt.get().getIsAccountValid()) {
                throw new InvalidCredentialsException("Esse CPF já está cadastrado!");
            }
        }

        return true;
    }

    /**
     * Login do Streamer
     * 
     * @param dto - Payload
     * @return - Retornar o token
     */
    public AuthResponseDto login(AuthRequestDto dto) {
        try {
            var streamerFind = repository.findByEmail(dto.email())
                    .orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado"));
            if (!streamerFind.getIsAccountValid()) {
                throw new JwtInvalidException("Streamer não autenticado!");
            }

            var info = infoStreamerRepository.findById(streamerFind.getId())
                    .orElseThrow(() -> new StreamerNotFoundException("Info do Streamer não encontrado"));
            info.setLastAccess(Instant.now());
            infoStreamerRepository.saveAndFlush(info);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.email(),
                            dto.password()));

            // 2. recupera usuário autenticado
            StreamerDetails userDetails = (StreamerDetails) authentication.getPrincipal();
            StreamerEntity streamer = userDetails.getStreamer();

            // 3. gera token e retorna o token com a expiracao
            return jwtUtil.generateTokens(streamer.getEmail());

        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Credenciais incorretas!");
        }
    }

    public RegisterResponseDto register(RegisterRequestDto dto) {
        validateCredentials(dto);

        var newStreamer = new StreamerEntity(
                dto.nickname(), 0.0, true, 5.0, 10, 100,
                dto.email(), passwordEncoder.encode(dto.password()),
                "ROLE_STREAMER", false, false, false, false);
        newStreamer.setIsAccountValid(false);
        newStreamer.setRegisteredAt(Instant.now());
        repository.saveAndFlush(newStreamer);

        var newInfo = new InfoStreamerEntity(
                newStreamer.getId(), dto.fullName(), dto.cpf(),
                null, Instant.now(), LocalDate.now(), 0, 0.0, false);
        infoStreamerRepository.saveAndFlush(newInfo);

        try {
            // Chamar o serviço SMTP corretamente
            EmailValidationResponseDTO emailResponse = emailValidationClient.createRegistrationValidation(
                    newStreamer.getId().toString(), // Converter Long para String
                    dto.email(),
                    dto.nickname());

            // Mapear resposta corretamente
            if (emailResponse != null && emailResponse.getSuccess()) {
                return new RegisterResponseDto(
                        HttpStatus.CREATED,
                        emailResponse.getMessage() != null ? emailResponse.getMessage()
                                : "Email de confirmação enviado com sucesso",
                        true);
            } else {
                return new RegisterResponseDto(
                        HttpStatus.BAD_REQUEST,
                        emailResponse != null ? emailResponse.getMessage() : "Falha no envio do email",
                        false);
            }

        } catch (Exception e) {
            throw new EmailSendException("Erro ao enviar email de confirmação: " + e.getMessage());
        }
    }

    /**
     * Confirmar email e ativar conta
     */
    @Transactional
    public SmtpResponseDto confirmEmail(String token) {
        try {
            // Validar token no serviço SMTP - usando o DTO do serviço SMTP
            EmailValidationResponseDTO response = emailValidationClient.validateToken(token);

            if (response != null && response.getSuccess()) {
                // Ativar conta do streamer usando o streamerId da resposta
                Long streamerId = (response.getStreamerId());
                StreamerEntity streamer = repository.findById(streamerId)
                        .orElseThrow(() -> new RuntimeException("Streamer não encontrado"));

                // Ativar a conta
                streamer.setIsAccountValid(true);
                repository.saveAndFlush(streamer);

                return new SmtpResponseDto(
                        HttpStatus.OK,
                        "Email confirmado com sucesso! Conta ativada.",
                        true,
                        streamer.getId().toString());

            } else {
                return new SmtpResponseDto(
                        HttpStatus.BAD_REQUEST,
                        response != null ? response.getMessage() : "Token inválido",
                        false,
                        null);
            }

        } catch (Exception e) {
            return new SmtpResponseDto(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Erro ao confirmar email: " + e.getMessage(),
                    false,
                    null);
        }
    }

    /**
     * Método auxiliar para ativar conta (se precisar usar separadamente)
     */
    @Transactional
    public void activateAccount(Long userId) {
        StreamerEntity streamer = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        streamer.setIsAccountValid(true);
        repository.save(streamer);
    }

}
