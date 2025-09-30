package tech.pierandrei.StreamPix.SmtpEmail.util;

import org.springframework.stereotype.Component;
import tech.pierandrei.StreamPix.SmtpEmail.entities.EmailValidationEntity;

@Component
public class EmailUtil {

    // Obter validação de sucesso de acordo com o type
    public String getValidationSuccessMessage(EmailValidationEntity.ValidationType type) {
        switch (type) {
            case ACCOUNT_ACTIVATION:
                return "Conta ativada com sucesso! Você já pode fazer login.";
            case PASSWORD_RECOVERY:
                return "Email validado! Agora você pode redefinir sua senha.";
            case EMAIL_CHANGE:
                return "Novo email confirmado com sucesso!";
            default:
                return "Email validado com sucesso!";
        }
    }


    // Obter mensagem de sucesso de acordo com o type
    public String getSuccessMessageByType(EmailValidationEntity.ValidationType type) {
        switch (type) {
            case ACCOUNT_ACTIVATION:
                return "Email de validação enviado com sucesso!";
            case PASSWORD_RECOVERY:
                return "Email de recuperação de senha enviado! Verifique sua caixa de entrada.";
            case EMAIL_CHANGE:
                return "Email de confirmação de mudança enviado!";
            default:
                return "Email enviado com sucesso!";
        }
    }
}
