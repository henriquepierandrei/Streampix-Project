package tech.pierandrei.StreamPix.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tech.pierandrei.StreamPix.entities.LogDonationsEntity;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LogDonationsRepository extends JpaRepository<LogDonationsEntity, Long>, JpaSpecificationExecutor<LogDonationsEntity> {
    Optional<LogDonationsEntity> findByTransactionId(String id);

    Optional<LogDonationsEntity> findByUuid(UUID idToUuid);

    Page<LogDonationsEntity> findByAmountBetweenAndDonatedAtBetween(
            Double minAmount,
            Double maxAmount,
            Instant startDate,
            Instant endDate,
            Pageable pageable
    );

}
