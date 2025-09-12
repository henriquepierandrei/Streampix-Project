package tech.pierandrei.StreamPix.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import tech.pierandrei.StreamPix.dtos.LogDonationResponseDTO;
import tech.pierandrei.StreamPix.entities.LogDonationsEntity;
import tech.pierandrei.StreamPix.entities.StatusDonation;
import tech.pierandrei.StreamPix.exceptions.InvalidValuesException;
import tech.pierandrei.StreamPix.repositories.LogDonationsRepository;
import tech.pierandrei.StreamPix.security.JwtUtil;
import tech.pierandrei.StreamPix.util.VariablesFormatted;

import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class LogDonationService {
    private final LogDonationsRepository logDonationsRepository;
    private final VariablesFormatted variablesFormatted;
    private final JwtUtil jwtUtil;


    public LogDonationService(LogDonationsRepository logDonationsRepository, VariablesFormatted variablesFormatted, JwtUtil jwtUtil) {
        this.logDonationsRepository = logDonationsRepository;
        this.variablesFormatted = variablesFormatted;
        this.jwtUtil = jwtUtil;
    }


    /**
     * Obtém as doações através de uma key de acesso simples
     * @param minAmount - Valor mínimo (opcional)
     * @param maxAmount - Valor máximo (opcional)
     * @param startDate - Data inicial (opcional)
     * @param endDate - Data final (opcional)
     * @param pageable - Paginação
     * @return Retorna os dados paginados
     */
    public Page<LogDonationResponseDTO> getLog(
            String token,
            Double minAmount,
            Double maxAmount,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    ) {
        var streamer = jwtUtil.getStreamerWithToken(token);


        Specification<LogDonationsEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (minAmount != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
            }
            if (maxAmount != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("donatedAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("donatedAt"), endDate));
            }

            predicates.add(cb.equal(root.get("statusDonation"), StatusDonation.SUCCESSFUL_PAYMENT));


            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<LogDonationsEntity> donations = logDonationsRepository.findAll(spec, pageable);

        return donations.map(log -> new LogDonationResponseDTO(
                log.getUuid(),
                log.getName(),
                log.getMessage(),
                variablesFormatted.formatDouble(log.getAmount()), // só formata na saída
                log.getDonatedAt(),
                log.getAudioUrl()
        ));
    }


}

