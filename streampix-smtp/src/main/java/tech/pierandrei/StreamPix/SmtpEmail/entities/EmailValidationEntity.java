package tech.pierandrei.StreamPix.SmtpEmail.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_validations")
public class EmailValidationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private long userId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "is_validated", nullable = false)
    private Boolean isValidated = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    // Métodos utilitários
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }

    public void markAsValidated() {
        this.isValidated = true;
        this.validatedAt = LocalDateTime.now();
    }

    // Construtores
    public EmailValidationEntity() {
        this.token = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.expiresAt = LocalDateTime.now().plusMinutes(30); // Expira em 30 minutos
    }

    public EmailValidationEntity(long userId, String email) {
        this();
        this.userId = userId;
        this.email = email;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Boolean getIsValidated() { return isValidated; }
    public void setIsValidated(Boolean validated) { isValidated = validated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getValidatedAt() { return validatedAt; }
    public void setValidatedAt(LocalDateTime validatedAt) { this.validatedAt = validatedAt; }
}