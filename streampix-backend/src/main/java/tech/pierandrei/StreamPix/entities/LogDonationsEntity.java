package tech.pierandrei.StreamPix.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Armazena o log das doações
 */
@Entity
@Table(name = "log_tb")
public class LogDonationsEntity {
    @Id
    private UUID uuid;

    private Long streamerId;

    private Double amount;

    private String name;

    @Column(length = 500)
    private String message;

    private String audioUrl;

    private Instant donatedAt;

    private String transactionId;

    private String qrCode;

    @Enumerated(EnumType.STRING)
    private StatusDonation statusDonation;

    // Getters And Setters

    public Long getStreamerId() {
        return streamerId;
    }

    public void setStreamerId(Long streamerId) {
        this.streamerId = streamerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getDonatedAt() {
        return donatedAt;
    }

    public void setDonatedAt(Instant donatedAt) {
        this.donatedAt = donatedAt;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public StatusDonation getStatusDonation() {
        return statusDonation;
    }

    public void setStatusDonation(StatusDonation statusDonation) {
        this.statusDonation = statusDonation;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public LogDonationsEntity(UUID uuid, Double amount, String name, String message, String audioUrl, Instant donatedAt, String transactionId, StatusDonation statusDonation, String qrCode) {
        this.uuid = uuid;
        this.amount = amount;
        this.name = name;
        this.message = message;
        this.audioUrl = audioUrl;
        this.donatedAt = donatedAt;
        this.transactionId = transactionId;
        this.statusDonation = statusDonation;
        this.qrCode = qrCode;
    }

    public LogDonationsEntity() {
    }
}
