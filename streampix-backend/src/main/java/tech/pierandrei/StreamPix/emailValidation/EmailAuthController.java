package tech.pierandrei.StreamPix.emailValidation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

    @PostMapping("/confirm-email")
    public ResponseEntity<?> confirmEmail(@RequestParam String token) {
        SmtpResponseDTO result = authService.confirmEmail(token);

        if (result.success()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result.message());
        }
    }

    @PostMapping("/resend-validation")
    public ResponseEntity<?> resendValidation(@RequestParam String email, @RequestParam String type) {
        try {
            // Converter manualmente
            ValidationTypeEnum enumType;
            try {
                enumType = ValidationTypeEnum.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(
                        new SmtpResponseDTO(false, "Tipo de validação inválido: " + type, null, -1, email));
            }

            // Buscar streamer pelo email
            StreamerEntity streamer = repository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (streamer.getIsAccountValid()) {
                return ResponseEntity.badRequest().body(
                        new RegisterResponseDTO(HttpStatus.BAD_REQUEST, "Conta já está ativada", false));
            }

            // Chamar serviço SMTP
            EmailValidationResponseDTO response = emailValidationClient.createRegistrationValidation(
                    streamer.getId().toString(),
                    email,
                    streamer.getNickname(),
                    enumType);

            RegisterResponseDTO result = new RegisterResponseDTO(
                    response.getSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST,
                    response.getMessage(),
                    response.getSuccess());

            return response.getSuccess() ? ResponseEntity.ok(result) : ResponseEntity.badRequest().body(result);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SmtpResponseDTO(false, "Erro ao processar a solicitação", null, -1, email));
        }
    }

    
    @GetMapping("/status/{streamerId}")
    public ResponseEntity<?> checkValidationStatus(@RequestParam String streamerId) {
        try {
            StreamerEntity streamer = repository.findById(streamerId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

            if (streamer.getIsAccountValid()) {
                return ResponseEntity.ok(new SmtpResponseDTO(true, "Conta já está ativada", null, streamerId, streamer.getEmail()));
            } else {
                return ResponseEntity.ok(new SmtpResponseDTO(false, "Conta ainda não está ativada", null, streamerId, streamer.getEmail()));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SmtpResponseDTO(false, "Erro ao processar a solicitação", null, -1, ""));
        }
    }
}
