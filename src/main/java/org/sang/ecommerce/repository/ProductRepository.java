package org.sang.ecommerce.repository;

import java.util.List;
import org.sang.ecommerce.model.Category;
import org.sang.ecommerce.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

	Page<Product> filterProducts(Specification<Product> spec, Pageable pageable);

	@Query("SELECT p From Product p Where LOWER(p.category.name)=:category")
	List<Product> findByCategory(@Param("category") String category);


//	@Query("""
//			SELECT p
//			FROM Product p
//			WHERE (p.category.name =:category OR :category='')
//			AND ((:minPrice IS NULL AND :maxPrice IS NULL) OR (p.discountedPrice BETWEEN :minPrice AND :maxPrice))
//			AND (:minDicsount IS NULL OR p.discountPercent >= :minDiscount)
//			ORDER BY
//			CASE WHEN :sort ='price_low' THEN p.discountedPrice END ASC,
//			CASE WHEN :sort='price_high' THEN p.discountedPrice END DESC
//			""")
//	List<Product> filterProducts(
//			@Param("category") String category,
//			@Param("minPrice") Integer minPrice,
//			@Param("maxPrice") Integer maxPrice,
//			@Param("minDiscount") Integer minDiscount,
//			@Param("sort") String sort
//	);

}
