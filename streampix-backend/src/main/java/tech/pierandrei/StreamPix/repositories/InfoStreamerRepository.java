package tech.pierandrei.StreamPix.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.pierandrei.StreamPix.entities.InfoStreamerEntity;

@Repository
public interface InfoStreamerRepository extends JpaRepository<InfoStreamerEntity, Long> {
    Optional<InfoStreamerEntity> findByStreamerId(long id);
    
    Optional<InfoStreamerEntity> findByCpf(String cpf);
}
