package tech.pierandrei.StreamPix.streamer;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InfoStreamerRepository extends JpaRepository<InfoStreamerEntity, String> {
    Optional<InfoStreamerEntity> findByStreamerId(String id);
    
    Optional<InfoStreamerEntity> findByCpf(String cpf);
}
