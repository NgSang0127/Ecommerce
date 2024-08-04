package org.sang.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.CartItemException;
import org.sang.ecommerce.model.CartItem;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.response.ApiResponse;
import org.sang.ecommerce.service.CartItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/cart-items")
public class CartItemController {

	private final CartItemService cartItemService;

	@DeleteMapping("/{cartItemId}")
	public ResponseEntity<ApiResponse> deleteCartItem(@PathVariable Long cartItemId, Authentication connectedUser)
			throws CartItemException {

		User user = ((User) connectedUser.getPrincipal());
		cartItemService.removeCartItem(user.getId(), cartItemId);

		ApiResponse res = ApiResponse.builder()
				.response("Item remove from cart successful")
				.build();

		return ResponseEntity.ok(res);
	}

	@PutMapping("/{cartItemId}")
	public ResponseEntity<CartItem> updateCartItem(
			@PathVariable Long cartItemId,
			@RequestBody CartItem cartItem,
			Authentication connectedUser
	) throws CartItemException {

		User user=((User)connectedUser.getPrincipal());
		CartItem updatedCartItem = cartItemService.updateCartItem(user.getId(),cartItemId,cartItem);

		return ResponseEntity.ok(updatedCartItem);
	}


}
