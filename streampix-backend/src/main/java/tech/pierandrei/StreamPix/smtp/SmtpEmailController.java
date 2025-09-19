package tech.pierandrei.StreamPix.smtp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tech.pierandrei.StreamPix.smtp.SmtpEmailEntity.ValidationTypeEnum;

@RestController
public class SmtpEmailController {
    @Autowired
    private SmtpEmailService emailService;

    @PostMapping("/smtp")
    public ResponseEntity<?> sentEmail(@RequestParam(name = "emailTo") String emailTo){
        return ResponseEntity.ok().body(emailService.registerEmailConfirmation(emailTo, ValidationTypeEnum.DELETE_ACCOUNT));
    }
}
