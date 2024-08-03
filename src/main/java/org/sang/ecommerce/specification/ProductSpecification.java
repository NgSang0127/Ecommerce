package org.sang.ecommerce.specification;

import org.sang.ecommerce.model.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

	public static Specification<Product> hasCategory(String category) {
		return (root, query, criteriaBuilder) ->
				category == null || category.isEmpty() ?
						criteriaBuilder.conjunction() :
						criteriaBuilder.equal(root.get("category").get("name"), category);
	}

	public static Specification<Product> hasPriceBetween(Double minPrice, Double maxPrice) {
		return (root, query, criteriaBuilder) -> {
			if (minPrice == null && maxPrice == null) {
				return criteriaBuilder.conjunction();
			}
			if (minPrice != null && maxPrice != null) {
				return criteriaBuilder.between(root.get("discountedPrice"), minPrice, maxPrice);
			}
			if (minPrice != null) {
				return criteriaBuilder.greaterThanOrEqualTo(root.get("discountedPrice"), minPrice);
			}
			return criteriaBuilder.lessThanOrEqualTo(root.get("discountedPrice"), maxPrice);
		};
	}

	public static Specification<Product> hasMinDiscount(Integer minDiscount) {
		return (root, query, criteriaBuilder) ->
				minDiscount == null ?
						criteriaBuilder.conjunction() :
						criteriaBuilder.greaterThanOrEqualTo(root.get("discountPercent"), minDiscount);
	}

	public static Specification<Product> applySort(String sort) {
		return (root, query, criteriaBuilder) -> {
			if (sort != null) {
				if (sort.equals("price_low")) {
					assert query != null;
					query.orderBy(criteriaBuilder.asc(root.get("discountedPrice")));
				} else if (sort.equals("price_high")) {
					assert query != null;
					query.orderBy(criteriaBuilder.desc(root.get("discountedPrice")));
				}
			}
			return criteriaBuilder.conjunction();
		};
	}

	// Specification for searching products by keyword
	public static Specification<Product> searchProduct(String keyword) {
		return (root, query, criteriaBuilder) -> {
			if (keyword == null || keyword.isEmpty()) {
				return criteriaBuilder.conjunction();
			}
			String searchKeyword = "%" + keyword.toLowerCase() + "%";
			return criteriaBuilder.or(
					criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchKeyword),
					criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchKeyword),
					criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")),searchKeyword),
					criteriaBuilder.like(criteriaBuilder.lower(root.get("category").get("name")),searchKeyword)
					// Add more fields as needed
			);
		};
	}
}
