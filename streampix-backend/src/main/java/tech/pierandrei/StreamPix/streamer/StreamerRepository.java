package tech.pierandrei.StreamPix.streamer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface StreamerRepository extends JpaRepository<StreamerEntity, String> {
    Optional<StreamerEntity> findByEmail(String email);

    Optional<StreamerEntity> findByNickname(String nickname);

    Optional<StreamerEntity> findById(String id);

    boolean existsByEmailAndIsAccountValid(String email, Boolean isAccountValid);

    boolean existsByNicknameAndIsAccountValid(String nickname, Boolean isAccountValid);


    @Modifying
    @Query("""
                DELETE FROM StreamerEntity s
                WHERE s.isAccountValid = false
                AND s.registeredAt < :threshold
            """)
    void deleteAllInvalidOlderThan(@Param("threshold") Instant threshold);

}
