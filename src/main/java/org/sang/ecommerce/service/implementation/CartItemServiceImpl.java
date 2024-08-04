package org.sang.ecommerce.service.implementation;

import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.CartItemException;
import org.sang.ecommerce.exception.UserException;
import org.sang.ecommerce.model.Cart;
import org.sang.ecommerce.model.CartItem;
import org.sang.ecommerce.model.Product;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.repository.CartItemRepository;
import org.sang.ecommerce.repository.CartRepository;
import org.sang.ecommerce.service.CartItemService;
import org.sang.ecommerce.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

	private final CartItemRepository cartItemRepository;
	private final CartRepository cartRepository;
	private final UserService userService;

	@Override
	public CartItem createCartItem(CartItem cartItem) {
		cartItem.setQuantity(1);
		cartItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
		cartItem.setDiscountedPrice((double) (cartItem.getProduct().getDiscountedPrice() * cartItem.getQuantity()));

		return cartItemRepository.save(cartItem);
	}

	@Override
	public CartItem updateCartItem(Long userId,Long cartItemId, CartItem cartItem) throws CartItemException,
			UserException {
		CartItem item = findCartItemById(cartItemId);
		User user = userService.findUserById(userId);
		if (user.getId().equals(userId)) {
			item.setQuantity(cartItem.getQuantity());
			item.setPrice(cartItem.getPrice());
			item.setDiscountedPrice((double) (item.getQuantity() * item.getProduct().getDiscountedPrice()));
			return cartItemRepository.save(item);
		} else {
			throw new CartItemException("You cannot update another user cart item");
		}
	}

	@Override
	public CartItem isCartItemExists(Cart cart, Product product, String size, Long userId) {
		return cartItemRepository.isCartItemExists(cart, product, size, userId);
	}

	@Override
	public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException {
		CartItem cartItem = findCartItemById(cartItemId);
		User user = userService.findUserById(cartItem.getUserId());
		User requestUser = userService.findUserById(userId);
		if (user.getId().equals(requestUser.getId())) {
			cartItemRepository.delete(cartItem);
		} else {

			throw new UserException("you can't remove another users item");

		}
	}

	@Override
	public CartItem findCartItemById(Long cartItemId) throws CartItemException {
		return cartItemRepository.findById(cartItemId).orElseThrow(
				()-> new CartItemException("Cart item not found with id "+cartItemId)
		);
	}
}
