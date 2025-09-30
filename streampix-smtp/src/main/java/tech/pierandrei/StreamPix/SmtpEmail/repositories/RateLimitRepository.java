package tech.pierandrei.StreamPix.SmtpEmail.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tech.pierandrei.StreamPix.SmtpEmail.entities.EmailValidationEntity;
import tech.pierandrei.StreamPix.SmtpEmail.entities.RateLimitEntity;
import java.time.LocalDateTime;

@Repository
public interface RateLimitRepository extends JpaRepository<RateLimitEntity, Long> {

    @Query("SELECT COUNT(r) FROM RateLimitEntity r WHERE r.userId = :userId " +
            "AND r.attemptTime BETWEEN :startTime AND :endTime")
    int countAttemptsByUserIdAndTimeRange(@Param("userId") String userId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(r) FROM RateLimitEntity r WHERE r.email = :email " +
            "AND r.attemptTime BETWEEN :startTime AND :endTime")
    int countAttemptsByEmailAndTimeRange(@Param("email") String email,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime);

    @Query("SELECT COUNT(r) FROM RateLimitEntity r WHERE r.userId = :userId " +
            "AND r.validationType = :type AND r.attemptTime BETWEEN :startTime AND :endTime")
    int countAttemptsByUserIdAndTypeAndTimeRange(@Param("userId") String userId,
                                                 @Param("type") EmailValidationEntity.ValidationType type,
                                                 @Param("startTime") LocalDateTime startTime,
                                                 @Param("endTime") LocalDateTime endTime);

    @Modifying
    @Query("DELETE FROM RateLimitEntity r WHERE r.attemptTime < :cutoffTime")
    int deleteOldAttempts(@Param("cutoffTime") LocalDateTime cutoffTime);
}