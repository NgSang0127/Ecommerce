package org.sang.ecommerce.service.implementation;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Product;
import org.sang.ecommerce.model.Review;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.repository.ReviewRepository;
import org.sang.ecommerce.request.ReviewRequest;
import org.sang.ecommerce.service.ProductService;
import org.sang.ecommerce.service.ReviewService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
	private final ReviewRepository reviewRepository;
	private final ProductService productService;

	@Override
	public Review createReview(ReviewRequest request, User user) throws ProductException {
		Product product=productService.findProductById(request.getProductId());
		Review review=Review.builder()
				.user(user)
				.product(product)
				.review(request.getReview())
				.build();
		return reviewRepository.save(review);
	}

	@Override
	public List<Review> getAllReviews(Long productId) {
		return reviewRepository.getAllReviewsProduct(productId);
	}
}
