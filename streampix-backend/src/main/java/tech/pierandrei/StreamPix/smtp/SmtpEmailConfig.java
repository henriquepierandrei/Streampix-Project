package tech.pierandrei.StreamPix.smtp;

import java.security.SecureRandom;
import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailConfig {
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.host}")
    private String mailHost;

    public record StatusEmailDTO(
            Boolean sentEmail,
            Instant sentTime) {
    }

    public StatusEmailDTO emailUtil(String emailTo, String code, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailTo);
            message.setFrom(mailHost);
            message.setSubject("Código de Autenticação");
            message.setText(content);

            mailSender.send(message);

            return new StatusEmailDTO(
                    true,
                    Instant.now());
        } catch (Exception e) {
            return new StatusEmailDTO(
                    false,
                    Instant.now());
        }
    }


    
}
