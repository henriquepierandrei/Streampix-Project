package tech.pierandrei.StreamPix.entities;

import java.time.Instant;
import java.util.UUID;

import org.jetbrains.annotations.NotNull;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity(name = "smtps_email_tb")
public class SmtpEmailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "streamer_id")
    private Long streamerId; // ID de identificação do streamer

    @NotNull
    private String code; // Código para verificar veracidade

    @Column(name = "sent_at")
    private Instant sentAt; // Data de envio do código

    @Column(name = "email_to")
    private String emailTo; // Email de quem recebeu o código

    @Column(name = "validation_type")
    private ValidationTypeEnum validationTypeEnum; // Tipo de solicitação do código

    @Column(name = "time_remaining_in_seconds")
    private Long timeRemainingInSeconds; // Tempo que resta para o código ser invalidado

    @Column(name = "code_is_validated")
    private Boolean codeIsValidated = false; // Código foi validado

    @Column(name = "attempts_number")
    private int attemptsNumber; // Número de tentativas

    // Verifica se o código é ou não válido
    public boolean isValidCode(String codeToVerify) {
        if (codeToVerify != null) {
            return this.code.equals(codeToVerify);
        }
        return false;
    }

    // Tipo de validação
    public enum ValidationTypeEnum {
        VERIFY_EMAIL_REGISTRATION, // Verificar o email quando for registrar
        WITHDRAW_BALANCE, // Confirmar por email o saque
        RECOVER_PASSWORD, // Recuperar a senha confirmando streamer por email
        DELETE_ACCOUNT // Deletar a conta do streamer somente se autorizar direto por email
    }

    // Constructor Default
    public SmtpEmailEntity() {
    }

    // Constructor
    public SmtpEmailEntity(Long streamerId, @NotNull String code, Instant sentAt, String emailTo,
            ValidationTypeEnum validationTypeEnum, Long timeRemainingInSeconds, Boolean codeIsValidated,
            int attemptsNumber) {
        this.streamerId = streamerId;
        this.code = code;
        this.sentAt = sentAt;
        this.emailTo = emailTo;
        this.validationTypeEnum = validationTypeEnum;
        this.timeRemainingInSeconds = timeRemainingInSeconds;
        this.codeIsValidated = codeIsValidated;
        this.attemptsNumber = attemptsNumber;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Long getStreamerId() {
        return streamerId;
    }

    public void setStreamerId(Long streamerId) {
        this.streamerId = streamerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    public String getEmailTo() {
        return emailTo;
    }

    public void setEmailTo(String emailTo) {
        this.emailTo = emailTo;
    }

    public ValidationTypeEnum getValidationTypeEnum() {
        return validationTypeEnum;
    }

    public void setValidationTypeEnum(ValidationTypeEnum validationTypeEnum) {
        this.validationTypeEnum = validationTypeEnum;
    }

    public Long getTimeRemainingInSeconds() {
        return timeRemainingInSeconds;
    }

    public void setTimeRemainingInSeconds(Long timeRemainingInSeconds) {
        this.timeRemainingInSeconds = timeRemainingInSeconds;
    }

    public Boolean getCodeIsValidated() {
        return codeIsValidated;
    }

    public void setCodeIsValidated(Boolean codeIsValidated) {
        this.codeIsValidated = codeIsValidated;
    }

    public int getAttemptsNumber() {
        return attemptsNumber;
    }

    public void setAttemptsNumber(int attemptsNumber) {
        this.attemptsNumber = attemptsNumber;
    }

}
