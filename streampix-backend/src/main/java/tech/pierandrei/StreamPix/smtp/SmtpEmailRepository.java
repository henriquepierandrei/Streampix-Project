package tech.pierandrei.StreamPix.smtp;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmtpEmailRepository extends JpaRepository<SmtpEmailEntity, UUID>{
    
}
