package tech.pierandrei.StreamPix.audio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import jakarta.transaction.Transactional;
import tech.pierandrei.StreamPix.streamer.StreamerRepository;

import java.time.Duration;
import java.time.Instant;

@Component
public class AudioKeepAliveTaskSchedule {
    private static final Logger log = LoggerFactory.getLogger(AudioKeepAliveTaskSchedule.class);
    private final StreamerRepository repository;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String healthUrl = "https://streampix-tts.onrender.com/health"; // coloque a URL completa

    public AudioKeepAliveTaskSchedule(StreamerRepository repository) {
        this.repository = repository;
    }

    

    @Scheduled(fixedRate = 10 * 60 * 1000) // 10 minutos
    public void keepAliveTTS() {
        try {
            String response = restTemplate.getForObject(healthUrl, String.class);
            log.info("✅ KeepAlive no TTS executado com sucesso! Response: {}", response);
        } catch (Exception e) {
            log.error("❌ Erro no KeepAlive", e);
        }
    }

    @Scheduled(fixedRate = 3600000) // a cada 1h
    @Transactional
    public void cleanupExpiredEmails() {
        Instant threshold = Instant.now().minus(Duration.ofHours(1));
        repository.deleteAllInvalidOlderThan(threshold);
    }

}