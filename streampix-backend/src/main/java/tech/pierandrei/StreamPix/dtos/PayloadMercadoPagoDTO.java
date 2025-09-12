package tech.pierandrei.StreamPix.dtos;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para extrair o response do Mercado Pago
 * @param transactionAmount - Valor da transação
 * @param description - Descrição da transação
 * @param paymentMethodId - Método de pagamento (Pix)
 * @param payer - Os dados do pagador
 */
public record PayloadMercadoPagoDTO(
        @JsonProperty("transaction_amount") Double transactionAmount,
        String description,
        @JsonProperty("payment_method_id") String paymentMethodId,

        Payer payer
) {
    public record Payer(
            String email,
            @JsonProperty("first_name") String firstName,
            @JsonProperty("last_name") String lastName
    ){}

    public PayloadMercadoPagoDTO(@JsonProperty("transaction_amount") Double transactionAmount, String description, @JsonProperty("payment_method_id") String paymentMethodId, Payer payer) {
        this.transactionAmount = transactionAmount;
        this.description = description;
        this.paymentMethodId = paymentMethodId;
        this.payer = payer;
    }
}
