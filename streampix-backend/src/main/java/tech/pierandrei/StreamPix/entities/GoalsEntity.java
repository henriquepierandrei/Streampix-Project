package tech.pierandrei.StreamPix.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "goals_tb")
public class GoalsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private BigDecimal currentBalance;      // Saldo Atual
    private BigDecimal balanceToAchieve;    // Saldo a alcançar
    private String reason;                  // Objetivo
    private Long userId;                    // A quem pertence
    private Instant endAt;                  // Termina em qual dia


    // Constructor
    public GoalsEntity() {
    }

    public GoalsEntity(BigDecimal currentBalance, BigDecimal balanceToAchieve, String reason, Long userId, Instant endAt) {
        this.currentBalance = currentBalance;
        this.balanceToAchieve = balanceToAchieve;
        this.reason = reason;
        this.userId = userId;
        this.endAt = endAt;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public BigDecimal getBalanceToAchieve() {
        return balanceToAchieve;
    }

    public void setBalanceToAchieve(BigDecimal balanceToAchieve) {
        this.balanceToAchieve = balanceToAchieve;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }
}
