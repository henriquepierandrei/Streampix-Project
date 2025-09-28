package tech.pierandrei.StreamPix.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StreamerDTO(
        @JsonProperty("id") Long id,
        @JsonProperty("nickname") String nickname,
        @JsonProperty("streamer_balance") String streamerBalance,
        @JsonProperty("email") String email,
        @JsonProperty("is_auto_play") Boolean isAutoPlay,
        @JsonProperty("min_amount") Double minAmount,
        @JsonProperty("max_characters_name") Integer maxCharactersName,
        @JsonProperty("max_characters_message") Integer maxCharactersMessage,
        @JsonProperty("qr_code_is_dark_theme") Boolean qrCodeIsDarkTheme,
        @JsonProperty("add_messages_bellow") Boolean addMessagesBellow,
        @JsonProperty("donate_is_dark_theme") Boolean donateIsDarkTheme,
        @JsonProperty("http_response") HttpResponseDefaultDTO dto,
        @JsonProperty("info_streamer") InfoStreamerDTO infoStreamer) {
}