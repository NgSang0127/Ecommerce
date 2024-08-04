package org.sang.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Cart;
import org.sang.ecommerce.model.CartItem;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.request.AddItemRequest;
import org.sang.ecommerce.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
	private final CartService cartService;

	@GetMapping
	public ResponseEntity<Cart> findUserCartHandler(
			Authentication connectedUser
	) {

		User user=((User)connectedUser.getPrincipal());
		Cart cart=cartService.findUserCart(user.getId());
		return ResponseEntity.ok(cart);
	}

	@PutMapping("/add")
	public ResponseEntity<CartItem> addItemToCart(
			@RequestBody AddItemRequest request,
			Authentication connectedUser
	) throws ProductException {
		User user=((User)connectedUser.getPrincipal());
		CartItem item=cartService.addCartItem(user.getId(), request);

		return ResponseEntity.ok(item);
	}
}
