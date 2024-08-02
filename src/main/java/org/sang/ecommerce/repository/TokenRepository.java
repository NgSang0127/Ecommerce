package org.sang.ecommerce.repository;

import java.util.List;
import java.util.Optional;
import org.sang.ecommerce.model.Token;
import org.sang.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
	@Query(value = """
      select t from Token t inner join User u\s
      on t.user.id = u.id\s
      where u.id = :id and (t.expired = false or t.revoked = false)\s
      """)
	List<Token> findAllValidTokenByUser(Long id);

	List<Token> findByUser(User user);

	Optional<Token> findByToken(String token);
}
