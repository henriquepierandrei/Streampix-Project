package tech.pierandrei.StreamPix.emailValidation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

@Component
public class EmailValidationClient {

    private final RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(EmailValidationClient.class);

    @Value("${smtp.service.url:http://localhost:8080}")
    private String emailValidationBaseUrl;

    public EmailValidationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Método para criar validação de registro
    public EmailValidationResponseDTO createRegistrationValidation(String streamerId, String email, String nickname,
            ValidationTypeEnum type) {
        String url = emailValidationBaseUrl + "/api/email-validation/send";

        try {
            // Criando parâmetros para o POST
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("userId", streamerId);
            params.add("email", email);
            params.add("nickname", nickname);
            params.add("type", type.name());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<EmailValidationResponseDTO> response = restTemplate.postForEntity(
                    url, request, EmailValidationResponseDTO.class);

            log.info("Validação de email criada com sucesso para: {}", email);
            return response.getBody();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Erro HTTP ao criar validação de email: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("Erro ao criar validação de email: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao chamar microserviço de email: {}", e.getMessage());
            throw new RuntimeException("Erro na comunicação com o serviço de email: " + e.getMessage());
        }
    }

    // Método para criar validação de recuperação de senha
    public EmailValidationResponseDTO createPasswordRecoveryValidation(String streamerId, String email) {
        String url = emailValidationBaseUrl + "/api/validation/password-recovery";

        try {
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("streamerId", streamerId);
            params.add("email", email);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<EmailValidationResponseDTO> response = restTemplate.postForEntity(
                    url, request, EmailValidationResponseDTO.class);

            log.info("Validação de recuperação de senha criada para: {}", email);
            return response.getBody();

        } catch (Exception e) {
            log.error("Erro ao criar validação de recuperação de senha: {}", e.getMessage());
            throw new RuntimeException("Erro ao criar validação de senha: " + e.getMessage());
        }
    }

    // Método para validar token
    public EmailValidationResponseDTO validateToken(String token) {
    String url = emailValidationBaseUrl + "/api/email-validation/validate?token=" + token;
    try {
        // Normalmente retorna 200 OK
        ResponseEntity<EmailValidationResponseDTO> response = restTemplate.getForEntity(
                url, EmailValidationResponseDTO.class);
        return response.getBody();
    } catch (HttpClientErrorException | HttpServerErrorException e) {
        // Desserializa o JSON do corpo de erro
        try {
            return new ObjectMapper().readValue(e.getResponseBodyAsString(), EmailValidationResponseDTO.class);
        } catch (Exception ex) {
            throw new RuntimeException("Erro ao processar resposta de erro: " + ex.getMessage(), ex);
        }
    } catch (Exception e) {
        throw new RuntimeException("Erro inesperado ao validar token: " + e.getMessage(), e);
    }
}


    // Método para verificar status do token
    @SuppressWarnings("unchecked")
    public Map<String, Object> checkTokenStatus(String token) {
        String url = emailValidationBaseUrl + "/api/validation/status?token=" + token;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            return (Map<String, Object>) response.getBody();

        } catch (Exception e) {
            log.error("Erro ao verificar status do token: {}", e.getMessage());
            throw new RuntimeException("Erro ao verificar status do token: " + e.getMessage());
        }
    }

    // Método para reenviar validação
    public EmailValidationResponseDTO resendValidation(Long validationId) {
        String url = emailValidationBaseUrl + "/api/validation/resend/" + validationId;

        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<EmailValidationResponseDTO> response = restTemplate.postForEntity(
                    url, request, EmailValidationResponseDTO.class);

            log.info("Validação reenviada com sucesso para ID: {}", validationId);
            return response.getBody();

        } catch (Exception e) {
            log.error("Erro ao reenviar validação: {}", e.getMessage());
            throw new RuntimeException("Erro ao reenviar validação: " + e.getMessage());
        }
    }
}