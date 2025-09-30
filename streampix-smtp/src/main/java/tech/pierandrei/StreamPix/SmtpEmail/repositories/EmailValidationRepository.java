package tech.pierandrei.StreamPix.SmtpEmail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.pierandrei.StreamPix.SmtpEmail.entities.EmailValidationEntity;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailValidationRepository extends JpaRepository<EmailValidationEntity, Long> {

    Optional<EmailValidationEntity> findByToken(String token);

    Optional<EmailValidationEntity> findByUserIdAndValidationTypeAndIsValidatedFalse(
            String userId, EmailValidationEntity.ValidationType validationType);

    boolean existsByUserIdAndValidationTypeAndIsValidatedTrue(
            String userId, EmailValidationEntity.ValidationType validationType);

    void deleteByUserIdAndValidationType(String userId, EmailValidationEntity.ValidationType validationType);

    @Query("DELETE FROM EmailValidationEntity e WHERE e.createdAt < :cutoffTime")
    @Modifying
    int deleteExpiredTokens(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT e FROM EmailValidationEntity e WHERE e.userId = :userId " +
            "AND e.validationType = :type ORDER BY e.createdAt DESC")
    Optional<EmailValidationEntity> findLastByUserIdAndValidationType(
            @Param("userId") String userId,
            @Param("type") EmailValidationEntity.ValidationType type);

    Optional<EmailValidationEntity> findByTokenAndUserId(String token, String streamerId);
}