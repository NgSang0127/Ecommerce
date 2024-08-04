package org.sang.ecommerce.service;

import org.sang.ecommerce.exception.CartItemException;
import org.sang.ecommerce.exception.UserException;
import org.sang.ecommerce.model.Cart;
import org.sang.ecommerce.model.CartItem;
import org.sang.ecommerce.model.Product;

public interface CartItemService {
	CartItem createCartItem(CartItem cartItem);

	CartItem updateCartItem(Long userId,Long cartItemId,CartItem cartItem) throws CartItemException, UserException;

	CartItem isCartItemExists(Cart cart, Product product,String size,Long userId);

	void removeCartItem(Long userId,Long cartItemId) throws CartItemException, UserException;

	CartItem findCartItemById(Long cartItemId) throws CartItemException;
}
