package tech.pierandrei.StreamPix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.pierandrei.StreamPix.entities.GoalsEntity;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalsRepository extends JpaRepository<GoalsEntity, UUID> {
    Optional<GoalsEntity> findByIdAndUserId(UUID goalsId, Long id);

    Optional<GoalsEntity> findByUserId(long l);
}
