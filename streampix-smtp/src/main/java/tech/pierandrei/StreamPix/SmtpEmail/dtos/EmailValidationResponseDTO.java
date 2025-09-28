package tech.pierandrei.StreamPix.SmtpEmail.dtos;

public class EmailValidationResponseDTO {
    private boolean success;
    private String message;
    private String token;
    private long streamerId;
    private String email;

    public EmailValidationResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public EmailValidationResponseDTO(boolean success, String message, String token, long streamerId, String email) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.streamerId = streamerId;
        this.email = email;
    }

    // Getters e Setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public long getStreamerId() { return streamerId; }
    public void setStreamerId(long streamerId) { this.streamerId = streamerId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}