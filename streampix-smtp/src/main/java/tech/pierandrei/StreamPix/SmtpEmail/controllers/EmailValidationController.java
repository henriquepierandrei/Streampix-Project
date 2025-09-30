package tech.pierandrei.StreamPix.SmtpEmail.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.pierandrei.StreamPix.SmtpEmail.dtos.EmailValidationResponseDTO;
import tech.pierandrei.StreamPix.SmtpEmail.entities.EmailValidationEntity;
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
            @RequestParam String userId,
            @RequestParam String email,
            @RequestParam(defaultValue = "Usuário") String nickname,
            @RequestParam EmailValidationEntity.ValidationType type) {

        EmailValidationResponseDTO response = validationService.createValidation(userId, email, nickname, type);

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
    public ResponseEntity<EmailValidationResponseDTO> validateEmail(@RequestParam String token, @RequestParam String streamerId) {
        EmailValidationResponseDTO response = validationService.validateToken(token, streamerId);

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
    public ResponseEntity<Boolean> checkValidationStatus(@PathVariable String userId) {
        boolean isValidated = validationService.isEmailValidated(userId);
        return ResponseEntity.ok(isValidated);
    }
}