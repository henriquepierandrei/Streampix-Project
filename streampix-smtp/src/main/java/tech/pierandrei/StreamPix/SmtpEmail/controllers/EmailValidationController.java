package tech.pierandrei.StreamPix.SmtpEmail.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.pierandrei.StreamPix.SmtpEmail.dtos.EmailValidationResponseDTO;
import tech.pierandrei.StreamPix.SmtpEmail.services.EmailValidationService;


@RestController
@RequestMapping("/api/email-validation")
public class EmailValidationController {

    private final EmailValidationService validationService;

    public EmailValidationController(EmailValidationService validationService) {
        this.validationService = validationService;
    }

    /**
     * Criar nova validação de email
     */
    @PostMapping("/send")
    public ResponseEntity<EmailValidationResponseDTO> sendValidationEmail(
            @RequestParam long userId,
            @RequestParam String email,
            @RequestParam(defaultValue = "Usuário") String userName) {

        EmailValidationResponseDTO response = validationService.createValidation(userId, email, userName);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Validar token (link clicado pelo usuário)
     */
    @GetMapping("/validate")
    public ResponseEntity<EmailValidationResponseDTO> validateEmail(@RequestParam String token) {
        EmailValidationResponseDTO response = validationService.validateToken(token);

        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Verificar se email foi validado
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<Boolean> checkValidationStatus(@PathVariable long userId) {
        boolean isValidated = validationService.isEmailValidated(userId);
        return ResponseEntity.ok(isValidated);
    }
}