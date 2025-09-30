package tech.pierandrei.StreamPix.SmtpEmail.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@streampix.com}")
    private String fromEmail;

    @Value("${frontend.url:http://localhost:3000}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private String verifyType(String type) {
        switch (type) {
            case "account_activation":
                return "Confirme seu email - StreamPix";
            case "PASSWORD_RESET":
                return "Redefinir senha - StreamPix";
            case "EMAIL_CHANGE":
                return "Confirmar mudança de email - StreamPix";
            default:
                return "Verificação - StreamPix";
        }
    }

    public void sendValidationEmail(String toEmail, String token, String nickname, String type, String streamerId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setFrom(fromEmail, "StreamPix");

            // Usa o método verifyType para definir o subject
            String subject = verifyType(type);
            helper.setSubject(subject);

            String validationLink = frontendUrl + "/email-auth/validate-email?streamerId=" + streamerId +"&token=" + token;

            // Personaliza o conteúdo baseado no tipo
            String welcomeMessage = getWelcomeMessage(type, nickname);
            String buttonText = getButtonText(type);

            String htmlBody = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>%s</title>
            </head>
            <body style="margin: 0; padding: 20px; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f8fafc; color: #334155;">
                <div style="max-width: 500px; margin: 0 auto; background: white; border-radius: 8px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1); overflow: hidden;">
                    
                    <!-- Header -->
                    <div style="padding: 32px 24px; text-align: center; border-bottom: 1px solid #e2e8f0;">
                        <img src="https://lh3.googleusercontent.com/a-/ALV-UjXx3Fft0GwNKUBf2wNhEP64JlXceR5pkAsbBg1H6ZoTUoeXyP4=s40-p" alt="StreamPix" style="width: 40px; height: 40px; margin-bottom: 12px;">
                        <h1 style="margin: 0; font-size: 24px; font-weight: 600; color: #6366f1;">
                            StreamPix
                        </h1>
                        <h2 style="margin: 8px 0 0; font-size: 18px; font-weight: 500; color: #475569;">
                            %s
                        </h2>
                    </div>
                    
                    <!-- Content -->
                    <div style="padding: 32px 24px;">
                        <p style="margin: 0 0 20px; font-size: 16px; line-height: 1.5; color: #475569;">
                            %s
                        </p>
                        
                        <!-- Verification Link Box -->
                        <div style="margin: 24px 0; padding: 20px; background-color: #f8fafc; border: 2px solid #e2e8f0; border-radius: 6px; text-align: center;">
                            <p style="margin: 0 0 16px; font-size: 14px; color: #64748b;">
                                Clique no botão abaixo para confirmar:
                            </p>
                            <a href="%s" style="display: inline-block; background-color: #6366f1; color: white; text-decoration: none; padding: 12px 28px; border-radius: 6px; font-size: 16px; font-weight: 600;">
                                %s
                            </a>
                        </div>
                        
                        <div style="margin: 24px 0 0; padding: 16px 0; border-top: 1px solid #e2e8f0;">
                            <p style="margin: 0 0 8px; font-size: 13px; color: #64748b;">
                                Este link expira em 30 minutos.
                            </p>
                            <p style="margin: 0; font-size: 13px; color: #64748b;">
                                Se você não fez esta solicitação, ignore este email.
                            </p>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div style="padding: 20px 24px; background-color: #f8fafc; border-top: 1px solid #e2e8f0; text-align: center;">
                        <p style="margin: 0; font-size: 12px; color: #94a3b8;">
                            Este email foi enviado automaticamente. Não responda a este email.
                        </p>
                        <p style="margin: 4px 0 0; font-size: 12px; color: #94a3b8;">
                            © 2024 StreamPix. Todos os direitos reservados.
                        </p>
                    </div>
                </div>
            </body>
            </html>
            """, subject, getHeaderTitle(type), welcomeMessage, validationLink, buttonText);

            helper.setText(htmlBody, true);
            mailSender.send(message);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Erro ao enviar email de validação: " + e.getMessage(), e);
        }
    }

    private String getWelcomeMessage(String type, String nickname) {
        String name = (nickname != null && !nickname.isEmpty()) ? nickname : "usuário";

        switch (type) {
            case "account_activation":
                return String.format("Olá %s! Bem-vindo ao StreamPix! Para completar seu cadastro, confirme seu email:", name);
            case "PASSWORD_RESET":
                return String.format("Olá %s! Recebemos uma solicitação para redefinir sua senha:", name);
            case "EMAIL_CHANGE":
                return String.format("Olá %s! Para confirmar a mudança do seu email, clique no link abaixo:", name);
            default:
                return String.format("Olá %s! Para verificar seu email, clique no link abaixo:", name);
        }
    }

    private String getButtonText(String type) {
        switch (type) {
            case "account_activation":
                return "Confirmar Email";
            case "PASSWORD_RESET":
                return "Redefinir Senha";
            case "EMAIL_CHANGE":
                return "Confirmar Mudança";
            default:
                return "Verificar";
        }
    }

    private String getHeaderTitle(String type) {
        switch (type) {
            case "account_activation":
                return "Confirme seu email";
            case "PASSWORD_RESET":
                return "Redefinir senha";
            case "EMAIL_CHANGE":
                return "Confirmar mudança de email";
            default:
                return "Verificação";
        }
    }
}