package tech.pierandrei.StreamPix.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.pierandrei.StreamPix.entities.StreamerEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StreamerRepository extends JpaRepository<StreamerEntity, Long> {
    Optional<StreamerEntity> findByEmail(String email);

    Optional<StreamerEntity> findByStreamerName(String streamerName);

    Optional<StreamerEntity> findById(Long id);

    boolean existsByEmail(String email);

    boolean existsByStreamerName(String streamerName);
}
