package tech.pierandrei.StreamPix.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.pierandrei.StreamPix.dtos.LogDonationResponseDTO;
import tech.pierandrei.StreamPix.exceptions.InvalidValuesException;
import tech.pierandrei.StreamPix.services.LogDonationService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/streamer/log")
public class LogDonationsController {
    private final LogDonationService logDonationService;

    public LogDonationsController(LogDonationService logDonationService) {
        this.logDonationService = logDonationService;
    }

    @GetMapping("/donations")
    public ResponseEntity<Page<LogDonationResponseDTO>> getLogs(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount,
            @RequestParam(required = false) String startDate, // recebe como String
            @RequestParam(required = false) String endDate,   // recebe como String
            Pageable pageable
    ) {
        // Obtenção dos tokens
        String token = authHeader.replace("Bearer ", "").trim();

        Instant startInstant = null;
        Instant endInstant = null;

        // Trasnforma a string em data
        if (startDate != null && !startDate.isBlank()) {
            startInstant = LocalDate.parse(startDate).atStartOfDay(ZoneOffset.UTC).toInstant();
        }
        if (endDate != null && !endDate.isBlank()) {
            endInstant = LocalDate.parse(endDate).atTime(23,59,59).atZone(ZoneOffset.UTC).toInstant();
        }

        Page<LogDonationResponseDTO> response = logDonationService.getLog(
                token, minAmount, maxAmount, startInstant, endInstant, pageable
        );

        return ResponseEntity.ok(response);
    }
}
