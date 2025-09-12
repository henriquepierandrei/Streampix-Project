package tech.pierandrei.StreamPix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.pierandrei.StreamPix.services.StreamerService;

@RestController
public class StreamerController {
    private final StreamerService streamerService;

    public StreamerController(StreamerService streamerService) {
        this.streamerService = streamerService;
    }

    /**
     * Obtém os dados do Streamer
     * @param authHeader - Bearer Token
     * @return - Retorna os dados do streamer
     * @throws Exception
     */
    @GetMapping("/streamer")
    public ResponseEntity<?> getStreamer(@RequestHeader("Authorization") String authHeader) throws Exception {
        String token = authHeader.replace("Bearer ", "").trim();
        var response = this.streamerService.getStreamerInfo(token);
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza os Dados do Streamer
     * @param authHeader - Bearer Token
     * @return - Retorna os dados do streamer
     * @throws Exception
     */
    @PutMapping("/streamer")
    public ResponseEntity<?> updateStreamer(@RequestHeader("Authorization") String authHeader, @RequestBody StreamerService.StreamerDTO streamerDTO) throws Exception {
        String token = authHeader.replace("Bearer ", "").trim();
        var response = this.streamerService.updateStreamerInfo(token, streamerDTO);
        return ResponseEntity.ok(response);
    }


    /**
     * Obter os dados do usuário para enviar doação
     * @param streamerName - Nome do Streamer
     * @return - Retorna os dados do streamer para doar
     * @throws Exception
     */
    @GetMapping("/{streamerName}")
    public ResponseEntity<?> getStreamerWithNameToDonate(@PathVariable String streamerName) throws Exception {
        var response = this.streamerService.getStreamerByName(streamerName);
        return ResponseEntity.ok(response);
    }


    /**
     * Obtém o tema do qrcode do usuário
     * @param streamerName - Nome do Streamer
     * @return - Retorna os dados do qr-code do Streamer
     * @throws Exception
     */
    @GetMapping("/streamer/qrcode")
    public ResponseEntity<?> getQrCodeTheme(@RequestParam String streamerName) throws Exception {
        var response = this.streamerService.getQrCodeTheme(streamerName);
        return ResponseEntity.ok(response);
    }
}