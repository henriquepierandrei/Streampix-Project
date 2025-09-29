package tech.pierandrei.StreamPix.gateway;

public record PaymentPayloadDTO(
        String transactionId,
        boolean isDonated,
        long timeRemainingSeconds
) {
}
