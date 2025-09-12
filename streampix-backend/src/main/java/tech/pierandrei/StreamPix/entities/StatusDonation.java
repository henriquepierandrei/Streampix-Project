package tech.pierandrei.StreamPix.entities;

/**
 * Status dos Donates
 */
public enum StatusDonation {
    SUCCESSFUL_PAYMENT,         // Pagamento foi concluído com sucesso
    UNSUCCESSFUL_PAYMENT,       // Pagamento expirado ou não realizado
    PENDING_PAYMENT,            // Pagamento pendente, ainda não foi enviado o pagamento
}
