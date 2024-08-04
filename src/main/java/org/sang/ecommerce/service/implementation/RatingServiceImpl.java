package org.sang.ecommerce.service.implementation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Product;
import org.sang.ecommerce.model.Rating;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.repository.RatingRepository;
import org.sang.ecommerce.request.RatingRequest;
import org.sang.ecommerce.service.ProductService;
import org.sang.ecommerce.service.RatingService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
	private final RatingRepository ratingRepository;
	private final ProductService productService;

	@Override
	public Rating createRating(RatingRequest request, User user) throws ProductException {
		Product product=productService.findProductById(request.getProductId());
		Rating rating=Rating.builder()
				.product(product)
				.user(user)
				.rating(request.getRating())
				.build();
		return ratingRepository.save(rating);
	}

	@Override
	public List<Rating> getRatingsProduct(Long productId) {

		return ratingRepository.getAllProductsRating(productId);
	}
}
