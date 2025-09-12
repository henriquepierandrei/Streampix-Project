package tech.pierandrei.StreamPix.dtos;

public record PaymentPayload(
        String transactionId,
        boolean isDonated,
        long timeRemainingSeconds
) {
}
