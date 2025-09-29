package tech.pierandrei.StreamPix.streamer;

import java.time.Instant;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import jakarta.persistence.*;
/**
 * Dados do streamer
 */
@Entity
@Table(name = "streamer_tb")
public class StreamerEntity {
    @Id
    private String id;

    @Column(unique = true)
    private String nickname;

    private Double streamerBalance;

    private Boolean isAutoPlay;

    private Double minAmount;
    private Integer maxCharactersName;
    private Integer maxCharactersMessage;

    @Column(unique = true)
    private String email;

    private String password; // senha criptografada
    private String role;     // Exemplo: "ROLE_STREAMER"

    // Tema do QrCode
    private Boolean qrCodeIsDarkTheme;
    private Boolean addMessagesBellow;

    // Tema da Doação
    private Boolean donateIsDarkTheme;

    // Conta está validada
    private Boolean isAccountValid;


    private Instant registeredAt;


    public StreamerEntity(String nickname, Double streamerBalance, Boolean isAutoPlay, Double minAmount,
            Integer maxCharactersName, Integer maxCharactersMessage, String email, String password, String role,
            Boolean qrCodeIsDarkTheme, Boolean addMessagesBellow, Boolean donateIsDarkTheme, Boolean isAccountValid) {
        this.nickname = nickname;
        this.streamerBalance = streamerBalance;
        this.isAutoPlay = isAutoPlay;
        this.minAmount = minAmount;
        this.maxCharactersName = maxCharactersName;
        this.maxCharactersMessage = maxCharactersMessage;
        this.email = email;
        this.password = password;
        this.role = role;
        this.qrCodeIsDarkTheme = qrCodeIsDarkTheme;
        this.addMessagesBellow = addMessagesBellow;
        this.donateIsDarkTheme = donateIsDarkTheme;
        this.isAccountValid = isAccountValid;
    }

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = NanoIdUtils.randomNanoId(); // Gera NanoID de 21 caracteres
        }
    }
    

    public StreamerEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getAccountValid() {
        return isAccountValid;
    }

    public void setAccountValid(Boolean accountValid) {
        isAccountValid = accountValid;
    }

    public Double getStreamerBalance() {
        return streamerBalance;
    }

    public void setStreamerBalance(Double streamerBalance) {
        this.streamerBalance = streamerBalance;
    }

    public Boolean getAutoPlay() {
        return isAutoPlay;
    }

    public void setAutoPlay(Boolean autoPlay) {
        isAutoPlay = autoPlay;
    }

    public Double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(Double minAmount) {
        this.minAmount = minAmount;
    }

    public Integer getMaxCharactersName() {
        return maxCharactersName;
    }

    public void setMaxCharactersName(Integer maxCharactersName) {
        this.maxCharactersName = maxCharactersName;
    }

    public Integer getMaxCharactersMessage() {
        return maxCharactersMessage;
    }

    public void setMaxCharactersMessage(Integer maxCharactersMessage) {
        this.maxCharactersMessage = maxCharactersMessage;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getQrCodeIsDarkTheme() {
        return qrCodeIsDarkTheme;
    }

    public void setQrCodeIsDarkTheme(Boolean qrCodeIsDarkTheme) {
        this.qrCodeIsDarkTheme = qrCodeIsDarkTheme;
    }

    public Boolean getAddMessagesBellow() {
        return addMessagesBellow;
    }

    public void setAddMessagesBellow(Boolean addMessagesBellow) {
        this.addMessagesBellow = addMessagesBellow;
    }

    public Boolean getDonateIsDarkTheme() {
        return donateIsDarkTheme;
    }

    public void setDonateIsDarkTheme(Boolean donateIsDarkTheme) {
        this.donateIsDarkTheme = donateIsDarkTheme;
    }

    public Boolean getIsAutoPlay() {
        return isAutoPlay;
    }

    public void setIsAutoPlay(Boolean isAutoPlay) {
        this.isAutoPlay = isAutoPlay;
    }

    public Boolean getIsAccountValid() {
        return isAccountValid;
    }

    public void setIsAccountValid(Boolean isAccountValid) {
        this.isAccountValid = isAccountValid;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }



    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }



    public String getNickname() {
        return nickname;
    }



    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    
    
}
