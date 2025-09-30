package tech.pierandrei.StreamPix.SmtpEmail.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import tech.pierandrei.StreamPix.SmtpEmail.repositories.RateLimitRepository;

import java.time.LocalDateTime;

public class CleanupLogsSchedule {
    private static final Logger log = LoggerFactory.getLogger(CleanupLogsSchedule.class);
    @Autowired
    private RateLimitRepository rateLimitRepository;


    @Scheduled(fixedRate = 3600000) // A cada hora
    @Transactional
    public void cleanupRateLimitLogs() {
        try {
            LocalDateTime cutoff = LocalDateTime.now().minusDays(2); // Manter logs por 2 dias
            int deleted = rateLimitRepository.deleteOldAttempts(cutoff);
            log.debug("Rate limit cleanup: {} registros removidos", deleted);
        } catch (Exception e) {
            log.error("Erro na limpeza dos logs de rate limiting: {}", e.getMessage());
        }
    }
}
