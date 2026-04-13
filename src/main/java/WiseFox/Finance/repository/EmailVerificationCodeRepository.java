package WiseFox.Finance.repository;

import WiseFox.Finance.model.EmailVerificationCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface EmailVerificationCodeRepository extends CrudRepository<EmailVerificationCode, Long> {

    // Find the latest valid (unused, non-expired) code for an email
    Optional<EmailVerificationCode> findTopByEmailOrderByExpiresAtDesc(String email);

    // Clean up all codes for an email once verified
    @Modifying
    @Transactional
    void deleteByEmail(String email);
}