package org.sang.ecommerce.service;

import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Cart;
import org.sang.ecommerce.model.CartItem;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.request.AddItemRequest;

public interface CartService {
	Cart createCart(User user);

	String addCartItem(Long userId, AddItemRequest request) throws ProductException;

	Cart findUserCart(Long userId);
}
