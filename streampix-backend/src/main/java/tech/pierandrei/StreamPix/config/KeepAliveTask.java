package tech.pierandrei.StreamPix.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class KeepAliveTask {
    private static final Logger log = LoggerFactory.getLogger(KeepAliveTask.class);
    private final DataSource dataSource;

    public KeepAliveTask(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Executa a cada 5 minutos
    @Scheduled(fixedRate = 150000)
    public void keepAlive() {
        try (Connection conn = dataSource.getConnection()) {
            conn.prepareStatement("SELECT 1").execute();
            log.info("✅ KeepAlive executado com sucesso!");
        } catch (Exception e) {
            log.error("❌ Erro no KeepAlive", e);
        }
    }
}