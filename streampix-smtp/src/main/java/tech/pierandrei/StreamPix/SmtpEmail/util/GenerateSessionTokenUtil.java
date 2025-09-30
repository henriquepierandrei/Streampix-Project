package tech.pierandrei.StreamPix.SmtpEmail.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Component
public class GenerateSessionTokenUtil {
    public String generateSessionToken(String streamerId) {
        try {
            // Usa SecureRandom para gerar bytes aleatórios criptograficamente seguros
            SecureRandom secureRandom = new SecureRandom();
            byte[] randomBytes = new byte[32]; // 256 bits de entropia
            secureRandom.nextBytes(randomBytes);

            // Adiciona timestamp para garantir unicidade temporal
            long timestamp = Instant.now().toEpochMilli();

            // Combina streamerId, timestamp e bytes aleatórios
            String dataToHash = streamerId + ":" + timestamp + ":" +
                    Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

            // Aplica SHA-256 para gerar hash seguro
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(dataToHash.getBytes(StandardCharsets.UTF_8));

            // Codifica em Base64 URL-safe (sem padding)
            String token = Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes);

            token = "streampix-token_" + token;
            return token;

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erro ao gerar token: SHA-256 não disponível", e);
        }
    }
}
