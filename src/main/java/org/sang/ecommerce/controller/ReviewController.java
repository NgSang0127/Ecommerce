package org.sang.ecommerce.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Review;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.request.ReviewRequest;
import org.sang.ecommerce.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	@PostMapping("/create")
	public ResponseEntity<Review> createReview(@RequestBody ReviewRequest req, Authentication connectedUser)
			throws ProductException {
		User user = ((User) connectedUser.getPrincipal());
		Review review = reviewService.createReview(req, user);
		return ResponseEntity.ok(review);
	}

	@GetMapping("/product/{productId}")
	public ResponseEntity<List<Review>> getProductsReview(@PathVariable Long productId) {
		List<Review> reviews = reviewService.getAllReviews(productId);
		return ResponseEntity.ok(reviews);
	}
}
