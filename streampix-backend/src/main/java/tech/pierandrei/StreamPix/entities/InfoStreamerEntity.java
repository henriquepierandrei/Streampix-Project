package tech.pierandrei.StreamPix.entities;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "info_streamers_tb")
public class InfoStreamerEntity {
    @Id
    @Column(unique = true, nullable = false)
    private Long streamerId;

    private String fullName;

    @Column(unique = true, nullable = false)
    private String cpf;

    private String profileImageUrl;

    private Instant lastAccess; // último acesso

    private LocalDate dateOfRegistration;   // data de registro

    private Integer totalDonationsReceived; // total de doações recebidas

    private Double totalAmountReceived;     // valor total recebido em doações

    private Boolean receiveNotification;        // receber notificações


    public InfoStreamerEntity(Long streamerId, String fullName, String cpf, String profileImageUrl, Instant lastAccess,
            LocalDate dateOfRegistration, Integer totalDonationsReceived, Double totalAmountReceived,
            Boolean receiveNotification) {
        this.streamerId = streamerId;
        this.fullName = fullName;
        this.cpf = cpf;
        this.profileImageUrl = profileImageUrl;
        this.lastAccess = lastAccess;
        this.dateOfRegistration = dateOfRegistration;
        this.totalDonationsReceived = totalDonationsReceived;
        this.totalAmountReceived = totalAmountReceived;
        this.receiveNotification = receiveNotification;
    }

    public InfoStreamerEntity() {
    }

    
    public Long getStreamerId() {
        return streamerId;
    }

    public void setStreamerId(Long streamerId) {
        this.streamerId = streamerId;
    }

    public Instant getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Instant lastAccess) {
        this.lastAccess = lastAccess;
    }

    public LocalDate getDateOfRegistration() {
        return dateOfRegistration;
    }

    public void setDateOfRegistration(LocalDate dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public Integer getTotalDonationsReceived() {
        return totalDonationsReceived;
    }

    public void setTotalDonationsReceived(Integer totalDonationsReceived) {
        this.totalDonationsReceived = totalDonationsReceived;
    }

    public Double getTotalAmountReceived() {
        return totalAmountReceived;
    }

    public void setTotalAmountReceived(Double totalAmountReceived) {
        this.totalAmountReceived = totalAmountReceived;
    }


    public Boolean getReceiveNotification() {
        return receiveNotification;
    }

    public void setReceiveNotification(Boolean receiveNotification) {
        this.receiveNotification = receiveNotification;
    }




    public String getFullName() {
        return fullName;
    }




    public void setFullName(String fullName) {
        this.fullName = fullName;
    }




    public String getCpf() {
        return cpf;
    }




    public void setCpf(String cpf) {
        this.cpf = cpf;
    }




    public String getProfileImageUrl() {
        return profileImageUrl;
    }




    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}
