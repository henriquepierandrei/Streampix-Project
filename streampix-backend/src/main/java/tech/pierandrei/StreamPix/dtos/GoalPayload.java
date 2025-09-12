package tech.pierandrei.StreamPix.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL) // → só inclui no JSON se não for null.
public record GoalPayload(
        String uuid,
        @JsonProperty("current_balance") BigDecimal currentBalance,      // Saldo Atual
        @JsonProperty("balance_to_achieve") BigDecimal balanceToAchieve,  // Saldo a alcançar
        String reason,               // Objetivo
        @JsonProperty("end_at_in_days") Integer endAtInDays
) {
}
