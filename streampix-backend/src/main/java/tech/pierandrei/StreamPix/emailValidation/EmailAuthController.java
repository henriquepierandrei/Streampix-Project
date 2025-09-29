package tech.pierandrei.StreamPix.emailValidation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tech.pierandrei.StreamPix.security.dtos.RegisterResponseDTO;
import tech.pierandrei.StreamPix.security.AuthService;
import tech.pierandrei.StreamPix.streamer.StreamerEntity;
import tech.pierandrei.StreamPix.streamer.StreamerRepository;

@RestController
@RequestMapping("/email-auth")
public class EmailAuthController {

    private final AuthService authService;
    private final StreamerRepository repository;
    private final EmailValidationClient emailValidationClient;

    public EmailAuthController(AuthService authService, StreamerRepository repository,
            EmailValidationClient emailValidationClient) {
        this.authService = authService;
        this.repository = repository;
        this.emailValidationClient = emailValidationClient;
    }

    // CONTROLLER CORRIGIDO
    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestParam String token) {
        SmtpResponseDTO result = authService.confirmEmail(token);

        if (result.sentEmail()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(result.httpStatus()).body(result);
        }
    }

    @PostMapping("/resend-validation/register")
    public ResponseEntity<?> resendValidation(@RequestParam String email) {
        try {
            // Buscar streamer pelo email
            StreamerEntity streamer = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (streamer.getIsAccountValid()) {
                return ResponseEntity.badRequest().body(new RegisterResponseDTO(
                        HttpStatus.BAD_REQUEST,
                        "Conta já está ativada",
                        false));
            }

            // Fazer chamada para o serviço SMTP
            EmailValidationResponseDTO response = emailValidationClient.createRegistrationValidation(
                    streamer.getId().toString(), // Converter Long para String
                    email,
                    streamer.getNickname(),
                    ValidationTypeEnum.ACCOUNT_ACTIVATION);

            // Mapear corretamente a resposta (Reutilizando o mesmo DTO do registro)
            RegisterResponseDTO result = new RegisterResponseDTO(
                    response.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST,
                    response.getMessage(),
                    response.getSuccess());

            if (response.getSuccess()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SmtpResponseDTO(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Erro ao reenviar email: " + e.getMessage(),
                            false,
                            null));
        }
    }
}
