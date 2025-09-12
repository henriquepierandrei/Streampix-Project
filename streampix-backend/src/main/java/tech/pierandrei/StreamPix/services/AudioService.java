package tech.pierandrei.StreamPix.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.pierandrei.StreamPix.dtos.DonationFullRequestDto;
import tech.pierandrei.StreamPix.exceptions.APIVoiceException;
import tech.pierandrei.StreamPix.exceptions.InvalidValuesException;

import java.util.UUID;

@Service
public class AudioService {

    private static final Logger log = LoggerFactory.getLogger(AudioService.class);
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Url da api responsável pela geração da voz
    @Value("${api.ai.voice.generator.url}")
    private String url;



    /**
     * Gera o áudio através da biblioteca edge-tts na api python
     * @return - Retorna a URL do áudio
     */
    public String generateAudio(UUID uuid, DonationFullRequestDto audioRequestDto) {
        // Extrai os dados do DTO
        String name = audioRequestDto.name();
        Double amount = audioRequestDto.amount();
        String text = audioRequestDto.message();
        DonationFullRequestDto.SettingsAIVoiceDto settings = audioRequestDto.SettingsAIVoice();

        // Monta o texto final com o valor
        String finalText;
        if (amount < 1) {
            int centavos = (int) Math.round(amount * 100);
            finalText = name + " enviou " + centavos + " centavos! " + text;
        } else if (amount == 1) {
            finalText = name + " enviou 1 real! " + text;
        } else {
            String valorFormatado = (amount % 1 == 0) ? String.valueOf(amount.doubleValue()) : String.valueOf(amount);
            finalText = name + " enviou " + valorFormatado + " reais! " + text;
        }

        // Valida a voz
        if (settings.voice_type() == null || settings.voice_type().isBlank()) {
            throw new InvalidValuesException("A voz precisa ser especificada.");
        }

        // Cria JSON payload usando o ObjectMapper para garantir o formato correto
        try {
            // Monta um objeto Map para o JSON
            var payloadMap = new java.util.HashMap<String, Object>();
            payloadMap.put("uuid", uuid.toString());
            payloadMap.put("text", finalText);
            payloadMap.put("voice_type", settings.voice_type());
            payloadMap.put("rate", settings.rate() != null ? "+" + settings.rate() + "%" : "+0%");
            if (settings.pitch() != null) {
                String pitchValue = settings.pitch() >= 0
                        ? "+" + settings.pitch() + "Hz"
                        : settings.pitch() + "Hz";
                payloadMap.put("pitch", pitchValue);
            } else {
                payloadMap.put("pitch", "+0Hz");
            }
            payloadMap.put("volume", settings.volume() != null ? "+" + settings.volume() + "%" : "+0%");
            payloadMap.put("style", settings.style() != null ? settings.style() : "cheerful");
            payloadMap.put("styledegree", settings.styledegree() != null ? settings.styledegree() : 1);

            String jsonPayload = objectMapper.writeValueAsString(payloadMap);

            // Configura headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

            // Envia para a API Python
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
            log.info("Áudio gerado com sucesso: {}", response.getBody());
            return response.getBody();

        } catch (Exception e) {
            log.error("Erro ao gerar áudio: {}", e.getMessage(), e);
            throw new APIVoiceException("Erro ao gerar áudio!");
        }
    }

}