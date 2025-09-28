package tech.pierandrei.StreamPix.SmtpEmail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tech.pierandrei.StreamPix.SmtpEmail.entities.EmailValidationEntity;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailValidationRepository extends JpaRepository<EmailValidationEntity, Long> {

    Optional<EmailValidationEntity> findByToken(String token);

    Optional<EmailValidationEntity> findByUserIdAndIsValidatedFalse(long userId);

    boolean existsByUserIdAndIsValidatedTrue(long userId);

    @Modifying
    @Transactional
    @Query("DELETE FROM EmailValidationEntity e WHERE e.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);
}