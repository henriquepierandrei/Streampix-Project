package tech.pierandrei.StreamPix.SmtpEmail.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.pierandrei.StreamPix.SmtpEmail.dtos.EmailValidationResponseDTO;
import tech.pierandrei.StreamPix.SmtpEmail.entities.EmailValidationEntity;
import tech.pierandrei.StreamPix.SmtpEmail.repositories.EmailValidationRepository;
import tech.pierandrei.StreamPix.SmtpEmail.util.EmailUtil;
import tech.pierandrei.StreamPix.SmtpEmail.util.GenerateSessionTokenUtil;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class EmailValidationService {
    private static final Logger logger = LoggerFactory.getLogger(EmailValidationService.class);
    private final EmailValidationRepository repository;
    private final EmailService emailService;
    private final RateLimitService checkRateService;
    private final EmailUtil emailUtil;
    private final GenerateSessionTokenUtil generateSessionTokenUtil;

    private static final long COOLDOWN_MINUTES = 15; // Tempo mínimo entre requests

    public EmailValidationService(EmailValidationRepository repository, EmailService emailService, RateLimitService checkRateService, EmailUtil emailUtil, GenerateSessionTokenUtil generateSessionTokenUtil) {
        this.repository = repository;
        this.emailService = emailService;
        this.checkRateService = checkRateService;
        this.emailUtil = emailUtil;
        this.generateSessionTokenUtil = generateSessionTokenUtil;
    }

    @Transactional
    public EmailValidationResponseDTO createValidation(String userId, String email,
            String nickname, EmailValidationEntity.ValidationType type) {
        try {
            // Verificar rate limiting ANTES de qualquer operação
            RateLimitService.RateLimitResult rateLimitResult = checkRateService.checkRateLimit(userId, email, type);
            if (!rateLimitResult.isAllowed()) {
                return new EmailValidationResponseDTO(false, rateLimitResult.getMessage());
            }

            // Para recuperação de senha, verificar cooldown
            if (type == EmailValidationEntity.ValidationType.PASSWORD_RECOVERY) {
                Optional<EmailValidationEntity> lastValidation = repository.findLastByUserIdAndValidationType(userId,
                        type);

                if (lastValidation.isPresent()) {
                    LocalDateTime lastAttempt = lastValidation.get().getCreatedAt();
                    LocalDateTime cooldownEnd = lastAttempt.plusMinutes(COOLDOWN_MINUTES);

                    if (LocalDateTime.now().isBefore(cooldownEnd)) {
                        long minutesLeft = ChronoUnit.MINUTES.between(LocalDateTime.now(), cooldownEnd);
                        return new EmailValidationResponseDTO(false,
                                String.format("Aguarde %d minutos antes de solicitar outro email de recuperação.",
                                        minutesLeft));
                    }
                }
                // Remove validações anteriores do mesmo tipo (só se passou do cooldown)
                repository.deleteByUserIdAndValidationType(userId, type);
            } else {
                // Lógica original para outros tipos
                Optional<EmailValidationEntity> existingValidation = repository
                        .findByUserIdAndValidationTypeAndIsValidatedFalse(userId, type);

                if (existingValidation.isPresent() && !existingValidation.get().isExpired()) {
                    return new EmailValidationResponseDTO(false,
                            "Já existe uma validação pendente para este tipo.");
                }
            }

            // Verificações específicas por tipo
            if (type == EmailValidationEntity.ValidationType.ACCOUNT_ACTIVATION &&
                    repository.existsByUserIdAndValidationTypeAndIsValidatedTrue(userId, type)) {
                return new EmailValidationResponseDTO(false,
                        "Este email já foi validado anteriormente.");
            }

            // Registrar tentativa no rate limiting
            checkRateService.recordAttempt(userId, email, type);

            // Criar nova validação
            EmailValidationEntity validation = new EmailValidationEntity(userId, email, type);
            validation.setToken(generateSessionTokenUtil.generateSessionToken(userId));
            validation = repository.save(validation);

            // Enviar email específico por tipo
            sendEmailByType(email, validation.getToken(), nickname, type, userId);

            logger.info("Validação criada - UserId: {}, Type: {}, Email: {}", userId, type, email);

            return new EmailValidationResponseDTO(true,
                    emailUtil.getSuccessMessageByType(type), null, userId, email);

        } catch (Exception e) {
            logger.error("Erro ao criar validação de email - UserId: {}, Type: {}: {}",
                    userId, type, e.getMessage());
            return new EmailValidationResponseDTO(false,
                    "Erro interno. Tente novamente mais tarde.");
        }
    }


    /**
     * Validar token de email (quando usuário clica no link)
     */
    @Transactional
    public EmailValidationResponseDTO validateToken(String token, String streamerId) {
        try {
            // Busca pelo token
            Optional<EmailValidationEntity> validation = repository.findByTokenAndUserId(token, streamerId);

            // Se o token não existir no db
            if (validation.isEmpty()) {
                logger.warn("Tentativa de validação com token inexistente: {}", token);
                return new EmailValidationResponseDTO(false, "Token inválido.");
            }

            EmailValidationEntity entity = validation.get();

            // Verifica se o token já foi validado
            if (entity.getValidated()) {
                logger.info("Tentativa de validação de token já validado - UserId: {}, Type: {}",
                        entity.getUserId(), entity.getValidationType());
                return new EmailValidationResponseDTO(false, "Email já foi validado anteriormente.");
            }

            // Verifica se o token expirou
            if (entity.isExpired()) {
                logger.info("Tentativa de validação de token expirado - UserId: {}, Type: {}",
                        entity.getUserId(), entity.getValidationType());

                // Remove token expirado para limpeza
                repository.delete(entity);

                return new EmailValidationResponseDTO(false,
                        "Token expirado. Solicite um novo código de validação.");
            }

            // Token válido - marcar como validado
            entity.markAsValidated();
            repository.save(entity);

            // Log de sucesso
            logger.info("Email validado com sucesso - UserId: {}, Type: {}, Email: {}",
                    entity.getUserId(), entity.getValidationType(), entity.getEmail());

            return new EmailValidationResponseDTO(
                    true,
                    emailUtil.getValidationSuccessMessage(entity.getValidationType()),
                    token,
                    entity.getUserId(),
                    entity.getEmail());

        } catch (Exception e) {
            logger.error("Erro ao validar token {}: {}", token, e.getMessage(), e);
            return new EmailValidationResponseDTO(false, "Erro interno. Tente novamente.");
        }
    }

    /**
     * Verificar se email foi validado para um usuário específico
     */
    public boolean isEmailValidated(String userId) {
        try {
            // Verifica se existe validação de ativação de conta validada
            boolean accountActivated = repository.existsByUserIdAndValidationTypeAndIsValidatedTrue(
                    userId, EmailValidationEntity.ValidationType.ACCOUNT_ACTIVATION);

            logger.debug("Status de validação para usuário {}: {}", userId, accountActivated);

            return accountActivated;

        } catch (Exception e) {
            logger.error("Erro ao verificar status de validação para usuário {}: {}", userId, e.getMessage());
            return false; // Em caso de erro, assume como não validado por segurança
        }
    }


    /**
     * Verificar se email foi validado por tipo específico
     */
    public boolean isEmailValidatedByType(String userId, EmailValidationEntity.ValidationType type) {
        try {
            return repository.existsByUserIdAndValidationTypeAndIsValidatedTrue(userId, type);
        } catch (Exception e) {
            logger.error("Erro ao verificar validação por tipo - UserId: {}, Type: {}: {}",
                    userId, type, e.getMessage());
            return false;
        }
    }


    /**
     * Buscar informações da validação por token (útil para debugging)
     */
    public Optional<EmailValidationEntity> getValidationByToken(String token) {
        try {
            return repository.findByToken(token);
        } catch (Exception e) {
            logger.error("Erro ao buscar validação por token {}: {}", token, e.getMessage());
            return Optional.empty();
        }
    }


    // Resto dos métodos existentes...
    private void sendEmailByType(String email, String token, String nickname,
            EmailValidationEntity.ValidationType type, String streamerId) {
        switch (type) {
            case ACCOUNT_ACTIVATION:
                emailService.sendValidationEmail(email, token, nickname, "account_activation", streamerId);
                break;
            // case PASSWORD_RECOVERY:
            // emailService.sendPasswordResetEmail(email, token, nickname);
            // break;
            // case EMAIL_CHANGE:
            // emailService.sendEmailChangeValidation(email, token, nickname);
            // break;
        }
    }
}