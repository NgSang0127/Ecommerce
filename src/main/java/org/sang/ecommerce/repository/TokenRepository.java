package org.sang.ecommerce.repository;

import java.util.Optional;
import org.sang.ecommerce.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {
	Optional<Token> findByToken(String token);
}
