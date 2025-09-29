package tech.pierandrei.StreamPix.goals;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoalsRepository extends JpaRepository<GoalsEntity, UUID> {
    Optional<GoalsEntity> findByIdAndUserId(UUID goalsId, String id);

    Optional<GoalsEntity> findByUserId(String id);
}
