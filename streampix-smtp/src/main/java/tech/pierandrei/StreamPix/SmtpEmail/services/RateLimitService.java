package tech.pierandrei.StreamPix.SmtpEmail.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.pierandrei.StreamPix.SmtpEmail.entities.EmailValidationEntity;
import tech.pierandrei.StreamPix.SmtpEmail.entities.RateLimitEntity;
import tech.pierandrei.StreamPix.SmtpEmail.repositories.RateLimitRepository;

import java.time.LocalDateTime;

@Service
@Transactional
public class RateLimitService {
    @Autowired
    private RateLimitRepository rateLimitRepository;


    // Rate limiting configurations
    private static final int MAX_ATTEMPTS_PER_HOUR = 5;
    private static final int MAX_ATTEMPTS_PER_DAY = 10;


    public RateLimitResult checkRateLimit(String userId, String email, EmailValidationEntity.ValidationType type) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourAgo = now.minusHours(1);
        LocalDateTime oneDayAgo = now.minusDays(1);

        // Contadores por usuário
        int attemptsLastHour = rateLimitRepository.countAttemptsByUserIdAndTimeRange(
                userId, oneHourAgo, now);
        int attemptsLastDay = rateLimitRepository.countAttemptsByUserIdAndTimeRange(
                userId, oneDayAgo, now);

        // Contadores por email (para casos onde userId pode variar)
        int emailAttemptsLastHour = rateLimitRepository.countAttemptsByEmailAndTimeRange(
                email, oneHourAgo, now);
        int emailAttemptsLastDay = rateLimitRepository.countAttemptsByEmailAndTimeRange(
                email, oneDayAgo, now);

        // Verificar limites por usuário
        if (attemptsLastHour >= MAX_ATTEMPTS_PER_HOUR) {
            return new RateLimitService.RateLimitResult(false,
                    "Muitas tentativas. Aguarde 1 hora antes de tentar novamente.");
        }

        if (attemptsLastDay >= MAX_ATTEMPTS_PER_DAY) {
            return new RateLimitService.RateLimitResult(false,
                    "Limite diário excedido. Tente novamente amanhã.");
        }

        // Verificar limites por email (proteção adicional)
        if (emailAttemptsLastHour >= MAX_ATTEMPTS_PER_HOUR) {
            return new RateLimitService.RateLimitResult(false,
                    "Este email recebeu muitos códigos recentemente. Aguarde 1 hora.");
        }

        if (emailAttemptsLastDay >= MAX_ATTEMPTS_PER_DAY) {
            return new RateLimitService.RateLimitResult(false,
                    "Limite diário excedido para este email. Tente novamente amanhã.");
        }

        // Rate limiting específico para recuperação de senha (mais restritivo)
        if (type == EmailValidationEntity.ValidationType.PASSWORD_RECOVERY) {
            int passwordRecoveryAttempts = rateLimitRepository.countAttemptsByUserIdAndTypeAndTimeRange(
                    userId, type, oneHourAgo, now);

            if (passwordRecoveryAttempts >= 3) { // Máximo 3 por hora para senha
                return new RateLimitService.RateLimitResult(false,
                        "Muitas tentativas de recuperação de senha. Aguarde 1 hora.");
            }
        }

        return new RateLimitService.RateLimitResult(true, null);
    }

    public void recordAttempt(String userId, String email, EmailValidationEntity.ValidationType type) {
        RateLimitEntity attempt = new RateLimitEntity(userId, email, type);
        attempt.setAttemptTime(LocalDateTime.now());
        rateLimitRepository.save(attempt);
    }


    // Classes auxiliares
    public class RateLimitResult {
        private final boolean allowed;
        private final String message;

        public RateLimitResult(boolean allowed, String message) {
            this.allowed = allowed;
            this.message = message;
        }

        public boolean isAllowed() {
            return allowed;
        }

        public String getMessage() {
            return message;
        }
    }
}
