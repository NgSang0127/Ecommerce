package org.sang.ecommerce.service;

import java.util.List;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Review;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.request.ReviewRequest;

public interface ReviewService {
	Review createReview(ReviewRequest request, User user) throws ProductException;

	List<Review> getAllReviews(Long productId);
}
