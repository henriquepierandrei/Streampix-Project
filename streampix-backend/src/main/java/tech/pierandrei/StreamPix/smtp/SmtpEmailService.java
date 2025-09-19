package tech.pierandrei.StreamPix.smtp;

import java.security.SecureRandom;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class SmtpEmailService {
    private final SmtpEmailConfig config;

    
    public SmtpEmailService(SmtpEmailConfig config) {
        this.config = config;
    }


    private static final String ALPHA_NUMERIC = 
        "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQRSTUVWXYZ0123456789!@#";
    private static final SecureRandom RANDOM = new SecureRandom();




    private String generateCode(SmtpEmailEntity.ValidationTypeEnum validationTypeEnum) {
        int length;

        // Define o tamanho do código com base no tipo de validação
        switch (validationTypeEnum) {
            case VERIFY_EMAIL_REGISTRATION:
                length = 6; // código curto
                break;
            case RECOVER_PASSWORD:
                length = 8; // código um pouco maior
                break;
            case WITHDRAW_BALANCE:
                length = 12; // código mais robusto
                break;
            case DELETE_ACCOUNT:
                length = 16; // código mais robusto
                break;
            default:
                length = 6; // padrão
        }

        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHA_NUMERIC.length());
            code.append(ALPHA_NUMERIC.charAt(index));
        }

        return code.toString();
    }


    public SmtpResponseDto registerEmailConfirmation(String emailTo, SmtpEmailEntity.ValidationTypeEnum type){
        var code = generateCode(type);

        var response = config.emailUtil(emailTo, code, "Codigo: " + code);

        if (response.sentEmail()) {
            return new SmtpResponseDto(HttpStatus.OK, "Email enviado com sucesso", true);
        }
        return new SmtpResponseDto(HttpStatus.OK, "Email não enviado", false);
    }

}
