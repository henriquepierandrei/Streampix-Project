package tech.pierandrei.StreamPix.logDonations;

/**
 * Status dos Donates
 */
public enum LogStatusDonationEnum {
    SUCCESSFUL_PAYMENT,         // Pagamento foi concluído com sucesso
    UNSUCCESSFUL_PAYMENT,       // Pagamento expirado ou não realizado
    PENDING_PAYMENT,            // Pagamento pendente, ainda não foi enviado o pagamento
}
