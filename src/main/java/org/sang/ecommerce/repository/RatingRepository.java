package org.sang.ecommerce.repository;

import java.util.List;
import org.sang.ecommerce.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingRepository extends JpaRepository<Rating,Long> {

	@Query("""
			SELECT r
			FROM Rating r
			WHERE r.product.id =:productId
			""")
	List<Rating> getAllProductsRating(Long productId);
}
