package tech.pierandrei.StreamPix.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record DonationFullRequestDto(
        UUID uuid,
        String name,
        Double amount,
        String message,
        @JsonProperty("settings_ai_voice") SettingsAIVoiceDto SettingsAIVoice

){
    public record SettingsAIVoiceDto(
            @JsonProperty("voice_type") String voice_type,
            Integer rate,
            Integer pitch,
            Integer volume,
            String style,
            Integer styledegree
    ){}
}
