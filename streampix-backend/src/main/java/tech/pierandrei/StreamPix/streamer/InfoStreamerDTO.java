package tech.pierandrei.StreamPix.streamer;

import com.fasterxml.jackson.annotation.JsonProperty;

import tech.pierandrei.StreamPix.dtos.HttpResponseDefaultDTO;

import java.time.Instant;
import java.time.LocalDate;

public record InfoStreamerDTO(
        @JsonProperty("full_name") String fullName,
        @JsonProperty("cpf") String cpf,
        @JsonProperty("profile_image_url") String profileImageUrl,
        @JsonProperty("last_access") Instant lastAccess,
        @JsonProperty("date_of_registration") LocalDate dateOfRegistration,
        @JsonProperty("total_donations_received") Integer totalDonationsReceived,
        @JsonProperty("total_amount_received") Double totalAmountReceived,
        @JsonProperty("receive_notification") Boolean receiveNotification,
        @JsonProperty("http_response") HttpResponseDefaultDTO dto) {
}
