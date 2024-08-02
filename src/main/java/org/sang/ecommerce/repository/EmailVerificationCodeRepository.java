package org.sang.ecommerce.repository;

import java.util.Optional;
import org.sang.ecommerce.model.EmailVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode,Long> {
	Optional<EmailVerificationCode> findByCode(String token);
}
