package tech.pierandrei.StreamPix.SmtpEmail.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rate_limit_attempts")
public class RateLimitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_type", nullable = false)
    private EmailValidationEntity.ValidationType validationType;

    @Column(name = "attempt_time", nullable = false)
    private LocalDateTime attemptTime;

    @Column(name = "ip_address")
    private String ipAddress;

    public RateLimitEntity(String userId, String email, EmailValidationEntity.ValidationType validationType, LocalDateTime attemptTime, String ipAddress) {
        this.userId = userId;
        this.email = email;
        this.validationType = validationType;
        this.attemptTime = attemptTime;
        this.ipAddress = ipAddress;
    }

    public RateLimitEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public EmailValidationEntity.ValidationType getValidationType() {
        return validationType;
    }

    public void setValidationType(EmailValidationEntity.ValidationType validationType) {
        this.validationType = validationType;
    }

    public LocalDateTime getAttemptTime() {
        return attemptTime;
    }

    public void setAttemptTime(LocalDateTime attemptTime) {
        this.attemptTime = attemptTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public RateLimitEntity(String userId, String email, EmailValidationEntity.ValidationType validationType) {
        this.userId = userId;
        this.email = email;
        this.validationType = validationType;
    }
}