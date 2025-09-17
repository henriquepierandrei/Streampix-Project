package tech.pierandrei.StreamPix.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tech.pierandrei.StreamPix.dtos.GoalPayload;
import tech.pierandrei.StreamPix.dtos.HttpResponseDefaultDTO;
import tech.pierandrei.StreamPix.entities.GoalsEntity;
import tech.pierandrei.StreamPix.exceptions.GoalsAlreadyExistsException;
import tech.pierandrei.StreamPix.exceptions.GoalsException;
import tech.pierandrei.StreamPix.exceptions.StreamerNotFoundException;
import tech.pierandrei.StreamPix.repositories.GoalsRepository;
import tech.pierandrei.StreamPix.repositories.StreamerRepository;
import tech.pierandrei.StreamPix.security.JwtUtil;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@Transactional
public class GoalsService {
    private StreamerRepository streamerRepository;
    private final GoalsRepository goalsRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public GoalsService(GoalsRepository goalsRepository, StreamerRepository streamerRepository) {
        this.goalsRepository = goalsRepository;
        this.streamerRepository = streamerRepository;
    }

    /**
     * Cria Meta
     * @param token - Bearer Token
     * @param goalPayload - Dto para criar a meta
     * @return - Retorna um http response com uma mensagem de response
     */
    public HttpResponseDefaultDTO createGoal(String token, GoalPayload goalPayload){
        var streamer = jwtUtil.getStreamerWithToken(token);

        var oldGoal = this.goalsRepository.findByUserId(streamer.getId());
        if (!oldGoal.isPresent()){
            // Calcula o Instant do prazo a partir de agora
            Instant endAt = Instant.now().plus(goalPayload.endAtInDays(), ChronoUnit.DAYS);

            var newGoals = new GoalsEntity(
                    BigDecimal.ZERO,
                    goalPayload.balanceToAchieve(),
                    goalPayload.reason(),
                    streamer.getId(),
                    endAt // armazena o Instant calculado
            );

            this.goalsRepository.saveAndFlush(newGoals);

            return new HttpResponseDefaultDTO(
                    HttpStatus.OK,
                    "Meta criada com sucesso, identificação: " + newGoals.getId()
            );
        }

        throw new GoalsAlreadyExistsException("Já existe uma meta.");
    }

    /**
     * Obter os dados da meta
     * @param token - Bearer Token
     * @return - Retorna os dados da meta
     */
    public GoalPayload getGoal(String token) {
        var streamer = jwtUtil.getStreamerWithToken(token);

        var goal = this.goalsRepository.findByUserId(streamer.getId())
                .orElseThrow(() -> new GoalsException("Meta não encontrada ou foi encerrada!"));

        Instant now = Instant.now();
        Instant end = goal.getEndAt();

        Integer daysRemaining;
        if (end != null) {
            long diff = ChronoUnit.DAYS.between(now, end);
            daysRemaining = (int) Math.max(diff, 0); // nunca negativo
        } else {
            daysRemaining = null; // ou 0 se preferir
        }

        return new GoalPayload(
                String.valueOf(goal.getId()),
                streamer.getId(),
                goal.getCurrentBalance(),
                goal.getBalanceToAchieve(),
                goal.getReason(),
                daysRemaining
        );
    }

    /**
     * Atualiza a meta
     * @param token - Bearer Token
     * @param goalPayload - Dto para atualizar
     * @return - Retorna um http response com uma mensagem de response
     */
    public HttpResponseDefaultDTO updateGoal(String token, GoalPayload goalPayload){
        var streamer = jwtUtil.getStreamerWithToken(token);
        var goal = this.goalsRepository.findByIdAndUserId(UUID.fromString(goalPayload.uuid()), streamer.getId()).orElseThrow(() -> new GoalsException("Meta não encontrada ou foi encerrada!"));

        if (goalPayload.reason() != null && !goalPayload.reason().isBlank()) {
            goal.setReason(goalPayload.reason());
        }


        if (goalPayload.endAtInDays() != null) {
            Instant now = Instant.now();
            // Adiciona os dias ao momento atual
            Instant newEndAt = now.plus(goalPayload.endAtInDays(), ChronoUnit.DAYS);
            goal.setEndAt(newEndAt);
        }


        this.goalsRepository.saveAndFlush(goal);
        return new HttpResponseDefaultDTO(
                HttpStatus.OK,
                "Meta Atualizada com sucesso!"
        );
    }

    /**
     * Deleta a meta
     * @param token - Bearer Token
     * @return - Retorna um http response com uma mensagem de response
     */
    public HttpResponseDefaultDTO deleteGoal(String token) {
        var streamer = jwtUtil.getStreamerWithToken(token);
        var goal = this.goalsRepository.findByUserId(streamer.getId()).orElseThrow(() -> new GoalsException("Meta não encontrada ou foi encerrada!"));
        this.goalsRepository.delete(goal);
        return new HttpResponseDefaultDTO(
                HttpStatus.OK,
                "Meta excluída com sucesso!"
        );
    }

    /**
     * Obter a meta para ser exibida
     * @param streamerName - Nome para puxar a meta
     * @return - Retorna os dados da meta
     */
    public GoalPayload getGoalToShow(Long id) {
        var streamer = streamerRepository.findById(id).orElseThrow(() -> new StreamerNotFoundException("Streamer não encontrado!"));

        var goal = this.goalsRepository.findByUserId(streamer.getId())
                .orElseThrow(() -> new GoalsException("Meta não encontrada ou foi encerrada!"));

        Instant now = Instant.now();
        Instant end = goal.getEndAt();

        Integer daysRemaining;
        if (end != null) {
            long diff = ChronoUnit.DAYS.between(now, end);
            daysRemaining = (int) Math.max(diff, 0); // nunca negativo
        } else {
            daysRemaining = null; // ou 0 se preferir
        }

        return new GoalPayload(
                String.valueOf(goal.getId()),
                null,
                goal.getCurrentBalance(),
                goal.getBalanceToAchieve(),
                goal.getReason(),
                daysRemaining
        );
    }
}