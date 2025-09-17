package tech.pierandrei.StreamPix.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.pierandrei.StreamPix.dtos.DonationFullRequestDto;
import tech.pierandrei.StreamPix.services.DonationService;


@RestController
@RequestMapping("/donation")
public class DonationController {
    private final DonationService donationService;

    public DonationController(DonationService donationService) {
        this.donationService = donationService;
    }

    /**
     *
     * @return - Retorna o Json recebido da API mapeado para um DTO
     * @throws JsonProcessingException - Caso tenha algum problema no mapeamento do Json
     */
    @PostMapping
    public ResponseEntity<?> donation(@RequestBody DonationFullRequestDto dto , @RequestParam(name = "streamer-name") String streamerName) throws Exception {
        var response = this.donationService.donationService(dto, streamerName);
        return ResponseEntity.ok(response);
    }

    /**
     *
     * @param uuid = ID da doação
     * @return - Retorna o Json recebido da API mapeado para um DTO
     * @throws JsonProcessingException - Caso tenha algum problema no mapeamento do Json
     */
    @GetMapping("{uuid}")
    public ResponseEntity<?> donation(@PathVariable String uuid) throws Exception {
        var response = this.donationService.getDonationInfoToPay(uuid);
        return ResponseEntity.ok(response);
    }
}
