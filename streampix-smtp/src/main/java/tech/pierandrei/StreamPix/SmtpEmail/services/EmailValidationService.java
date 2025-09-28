package tech.pierandrei.StreamPix.SmtpEmail.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.pierandrei.StreamPix.SmtpEmail.dtos.EmailValidationResponseDTO;
import tech.pierandrei.StreamPix.SmtpEmail.entities.EmailValidationEntity;
import tech.pierandrei.StreamPix.SmtpEmail.repositories.EmailValidationRepository;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailValidationService {

    private static final Logger logger = LoggerFactory.getLogger(EmailValidationService.class);

    private final EmailValidationRepository repository;
    private final EmailService emailService;

    public EmailValidationService(EmailValidationRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    /**
     * Criar nova validação de email
     */
    @Transactional
    public EmailValidationResponseDTO createValidation(long userId, String email, String userName) {
        try {
            // Verificar se já existe uma validação válida pendente
            Optional<EmailValidationEntity> existingValidation =
                    repository.findByUserIdAndIsValidatedFalse(userId);

            // Se existir um email de validação pendente
            if (existingValidation.isPresent() && !existingValidation.get().isExpired()) {
                return new EmailValidationResponseDTO(
                        false,
                        "Já existe um email de validação pendente. Verifique sua caixa de entrada."
                );
            }

            // Verificar se o email já foi validado
            if (repository.existsByUserIdAndIsValidatedTrue(userId)) {
                return new EmailValidationResponseDTO(
                        false,
                        "Este email já foi validado anteriormente."
                );
            }

            // Remover validação anterior se existir e estiver expirada (Para remover dados inúteis do banco de dados)
            existingValidation.ifPresent(repository::delete);

            // Criar nova validação
            EmailValidationEntity validation = new EmailValidationEntity(userId, email);
            validation = repository.save(validation);

            // Enviar email
            emailService.sendValidationEmail(email, validation.getToken(), userName);

            logger.info("Validação de email criada para usuário");

            return new EmailValidationResponseDTO(
                    true,
                    "Email de validação enviado com sucesso!",
                    null,       // Token não pode ser usado no response
                    userId,
                    email
            );

        } catch (Exception e) {
            logger.error("Erro ao criar validação de email para usuário");
            return new EmailValidationResponseDTO(
                    false,
                    "Erro interno. Tente novamente mais tarde."
            );
        }
    }

    /**
     * Validar token de email
     */
    @Transactional
    public EmailValidationResponseDTO validateToken(String token) {
        try {
            // Busca pelo token
            Optional<EmailValidationEntity> validation = repository.findByToken(token);

            // Se o token não existir no db
            if (validation.isEmpty()) {
                return new EmailValidationResponseDTO(false, "Token inválido.");
            }

            // Valida o token (Se o token ja foi validado ou se foi expirado)
            EmailValidationEntity entity = validation.get();

            if (entity.getIsValidated()) {
                return new EmailValidationResponseDTO(false, "Email já foi validado anteriormente.");
            }

            if (entity.isExpired()) {
                return new EmailValidationResponseDTO(false, "Token expirado. Solicite um novo.");
            }

            // Marcar como validado
            entity.markAsValidated();
            repository.save(entity);

            logger.info("Email validado com sucesso para usuário");

            return new EmailValidationResponseDTO(
                    true,
                    "Email validado com sucesso!",
                    token,
                    entity.getUserId(),
                    entity.getEmail()
            );

        } catch (Exception e) {
            logger.error("Erro ao validar token {}: {}", token, e.getMessage());
            return new EmailValidationResponseDTO(false, "Erro interno. Tente novamente.");
        }
    }


    /**
     * Verificar se email foi validado
     */
    public boolean isEmailValidated(long userId) {
        return repository.existsByUserIdAndIsValidatedTrue(userId);
    }

    /**
     * Limpeza automática de tokens expirados (executa diariamente)
     */
    @Scheduled(fixedRate = 86400000) // 24 horas
    @Transactional
    public void cleanupExpiredTokens() {
        try {
            int deletedTokens = repository.deleteExpiredTokens(LocalDateTime.now());
            logger.info("Limpeza executada: {} tokens expirados removidos", deletedTokens);
        } catch (Exception e) {
            logger.error("Erro na limpeza de tokens expirados: {}", e.getMessage());
        }
    }
}