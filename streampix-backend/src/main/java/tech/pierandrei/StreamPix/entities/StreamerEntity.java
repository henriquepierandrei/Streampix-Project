package tech.pierandrei.StreamPix.entities;

import jakarta.persistence.*;

/**
 * Dados do streamer
 */
@Entity
@Table(name = "streamer_tb")
public class StreamerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String streamerName;
    private Double streamerBalance;
    private Boolean isAutoPlay;
    private Double minAmount;
    private Integer maxCharactersName;
    private Integer maxCharactersMessage;
    private String email;

    private String password; // 🔑 senha criptografada
    private String role;     // Exemplo: "ROLE_STREAMER"

    // Tema do QrCode
    private Boolean qrCodeIsDarkTheme;
    private Boolean addMessagesBellow;

    // Tema da Doação
    private Boolean donateIsDarkTheme;


    public StreamerEntity(String streamerName, Double streamerBalance, Boolean isAutoPlay, Double minAmount, Integer maxCharactersName, Integer maxCharactersMessage, String email, String password, String role, Boolean qrCodeIsDarkTheme, Boolean addMessagesBellow, Boolean donateIsDarkTheme) {
        this.streamerName = streamerName;
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
    }

    public StreamerEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreamerName() {
        return streamerName;
    }

    public void setStreamerName(String streamerName) {
        this.streamerName = streamerName;
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
}
