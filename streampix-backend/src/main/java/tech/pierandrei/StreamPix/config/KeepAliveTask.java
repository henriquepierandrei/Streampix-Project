package tech.pierandrei.StreamPix.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class KeepAliveTask {
    private static final Logger log = LoggerFactory.getLogger(KeepAliveTask.class);
    private final DataSource dataSource;


    private final RestTemplate restTemplate = new RestTemplate();
    private final String healthUrl = "https://streampix-tts.onrender.com/health"; // coloque a URL completa

    public KeepAliveTask(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    
    @Scheduled(fixedRate = 150000)
    public void keepAlive() {
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("SELECT 1").execute();
            log.info("✅ KeepAlive executado com sucesso!");
        } catch (Exception e) {
            log.error("❌ Erro no KeepAlive", e);
        }
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
}