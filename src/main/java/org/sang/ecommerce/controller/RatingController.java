package org.sang.ecommerce.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Rating;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.request.RatingRequest;
import org.sang.ecommerce.service.RatingService;
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
@RequestMapping("/api/ratings")
public class RatingController {

	private final RatingService ratingService;

	@PostMapping("/create")
	public ResponseEntity<Rating> createRating(@RequestBody RatingRequest req, Authentication connectedUser)
			throws ProductException {
		User user=((User) connectedUser.getPrincipal());
		Rating rating=ratingService.createRating(req, user);
		return ResponseEntity.ok(rating);
	}

	@GetMapping("/product/{productId}")
	public ResponseEntity<List<Rating>> getProductsReview(@PathVariable Long productId){

		List<Rating> ratings=ratingService.getRatingsProduct(productId);
		return ResponseEntity.ok(ratings);
	}
}
