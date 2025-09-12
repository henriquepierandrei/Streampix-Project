package tech.pierandrei.StreamPix.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.pierandrei.StreamPix.dtos.GoalPayload;
import tech.pierandrei.StreamPix.exceptions.GoalsException;
import tech.pierandrei.StreamPix.services.GoalsService;
import tech.pierandrei.StreamPix.websocket.WebSocketController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/streamer/goal")
public class GoalController {
    private static final Logger log = LoggerFactory.getLogger(GoalController.class);
    private final GoalsService goalsService;

    @Autowired
    private WebSocketController webSocketController;

    public GoalController(GoalsService goalsService) {
        this.goalsService = goalsService;
    }

    // ================== GET ==================
    @GetMapping
    public ResponseEntity<?> getGoal(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        var response = goalsService.getGoal(token);
        log.debug("Streamer buscando a meta.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/to-show")
    public ResponseEntity<?> getGoalToShow(@RequestParam String streamerName) {
        try {
            var response = goalsService.getGoalToShow(streamerName);
            log.debug("Streamer buscando a meta para exibir.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao buscar meta: ", e);
            return ResponseEntity.status(500).body("Erro interno: " + e.getMessage());
        }
    }

    // ================== POST ==================
    @PostMapping
    public ResponseEntity<?> createGoal(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody GoalPayload goalPayload) {
        String token = authHeader.replace("Bearer ", "").trim();
        var response = goalsService.createGoal(token, goalPayload);
        log.debug("Streamer criando uma meta.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/notify")
    public void notify(@RequestParam String name, @RequestParam double value) {
        webSocketController.notifyGoalIncrement("958af5c6-44c8-4e8c-91fe-317d2795928f", BigDecimal.valueOf(value));
    }

    // ================== PUT ==================
    @PutMapping
    public ResponseEntity<?> updateGoal(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody GoalPayload goalPayload) {
        String token = authHeader.replace("Bearer ", "").trim();
        var response = goalsService.updateGoal(token, goalPayload);
        log.debug("Streamer atualizando uma meta.");
        return ResponseEntity.ok(response);
    }

    // ================== DELETE ==================
    @DeleteMapping
    public ResponseEntity<?> deleteGoal(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "").trim();
        var response = goalsService.deleteGoal(token);
        log.debug("Streamer excluindo uma meta.");
        return ResponseEntity.ok(response);
    }
}
