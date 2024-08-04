package org.sang.ecommerce.repository;

import java.util.List;
import org.sang.ecommerce.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

	@Query("""
			SELECT r
			FROM Review r
			WHERE r.product.id =:productId
			""")
	List<Review> getAllReviewsProduct(Long productId);
}
