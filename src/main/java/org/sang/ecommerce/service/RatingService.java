package org.sang.ecommerce.service;

import java.util.List;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Rating;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.request.RatingRequest;

public interface RatingService {
	Rating createRating(RatingRequest request, User user) throws ProductException;

	List<Rating> getRatingsProduct(Long productId);

}
