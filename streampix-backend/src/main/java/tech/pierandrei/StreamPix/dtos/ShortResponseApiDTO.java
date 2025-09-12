package tech.pierandrei.StreamPix.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

/**
 * DTO responsável por exibir os dados para pagamento (QRCODE)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ShortResponseApiDTO {

    @JsonProperty("transaction_amount")
    private Double transactionAmount;

    @JsonProperty("point_of_interaction")
    private PointOfInteraction pointOfInteraction;

    @JsonProperty("qr_code_to_html_image")
    private String qrCodeToHtmlImage;

    @JsonProperty("transaction_StreamPix_id")
    private UUID transactionStreamPixId;

    @JsonProperty("id")
    private String id;

    public ShortResponseApiDTO() {}

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public PointOfInteraction getPointOfInteraction() {
        return pointOfInteraction;
    }

    public void setPointOfInteraction(PointOfInteraction pointOfInteraction) {
        this.pointOfInteraction = pointOfInteraction;
    }

    public UUID getTransactionStreamPixId() {
        return transactionStreamPixId;
    }

    public void setTransactionStreamPixId(UUID transactionStreamPixId) {
        this.transactionStreamPixId = transactionStreamPixId;
    }

    public String getQrCodeToHtmlImage() {
        return qrCodeToHtmlImage;
    }

    public void setQrCodeToHtmlImage(String qrCodeToHtmlImage) {
        this.qrCodeToHtmlImage = qrCodeToHtmlImage;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PointOfInteraction {

        @JsonProperty("transaction_data")
        private TransactionData transactionData;

        public PointOfInteraction() {}

        public TransactionData getTransactionData() {
            return transactionData;
        }

        public void setTransactionData(TransactionData transactionData) {
            this.transactionData = transactionData;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionData {

        @JsonProperty("qr_code")
        private String qrCode;

        @JsonProperty("qr_code_base64")
        private String qrCodeBase64;

        @JsonProperty("ticket_url")
        private String ticketUrl;

        public TransactionData() {}

        public String getQrCode() {
            return qrCode;
        }

        public void setQrCode(String qrCode) {
            this.qrCode = qrCode;
        }

        public String getQrCodeBase64() {
            return qrCodeBase64;
        }

        public void setQrCodeBase64(String qrCodeBase64) {
            this.qrCodeBase64 = qrCodeBase64;
        }

        public String getTicketUrl() {
            return ticketUrl;
        }

        public void setTicketUrl(String ticketUrl) {
            this.ticketUrl = ticketUrl;
        }
    }
}
