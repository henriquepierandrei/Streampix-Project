package tech.pierandrei.StreamPix.dtos;

public class EmailValidationResponseDTO {
    private String message;
    private String validationType;
    private String email;
    private long streamerId;
    private String token;
    private Boolean success;

    // Construtores
    public EmailValidationResponseDTO() {}

    public EmailValidationResponseDTO(String message, String validationType, String email, long streamerId, String token, Boolean success) {
        this.message = message;
        this.validationType = validationType;
        this.email = email;
        this.streamerId = streamerId;
        this.token = token;
        this.success = success;
    }

    // Getters e Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getValidationType() { return validationType; }
    public void setValidationType(String validationType) { this.validationType = validationType; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public long getStreamerId() { return streamerId; }
    public void setStreamerId(long streamerId) { this.streamerId = streamerId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
}
