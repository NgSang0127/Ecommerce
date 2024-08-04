package org.sang.ecommerce.service.implementation;

import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Cart;
import org.sang.ecommerce.model.CartItem;
import org.sang.ecommerce.model.Product;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.repository.CartItemRepository;
import org.sang.ecommerce.repository.CartRepository;
import org.sang.ecommerce.request.AddItemRequest;
import org.sang.ecommerce.service.CartItemService;
import org.sang.ecommerce.service.CartService;
import org.sang.ecommerce.service.ProductService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductService productService;
	private final CartItemService cartItemService;

	@Override
	public Cart createCart(User user) {
		return cartRepository.save(
				Cart.builder()
						.user(user)
						.build()
		);
	}

	@Override
	public String addCartItem(Long userId, AddItemRequest request) throws ProductException {
		Cart cart=cartRepository.findByUserId(userId);
		Product product=productService.findProductById(request.getProductId());
		CartItem isPresent=cartItemRepository.isCartItemExists(cart,product, request.getSize(), userId);
		if(isPresent == null){
			double price=request.getQuantity()*product.getDiscountedPrice();
			var cartItem=CartItem.builder()
					.product(product)
					.cart(cart)
					.quantity(request.getQuantity())
					.userId(userId)
					.price(price)
					.size(request.getSize())
					.build();
			CartItem createdCartItem=cartItemService.createCartItem(cartItem);
			cart.getCartItems().add(createdCartItem);
		}

		return "Item add to card successful";
	}

	@Override
	public Cart findUserCart(Long userId) {
		Cart cart=cartRepository.findByUserId(userId);
		double totalPrice=0;
		double totalDiscountedPrice=0;
		int totalItem=0;
		for (CartItem cartItem : cart.getCartItems()
			 ) {
			totalPrice+=cartItem.getPrice();
			totalDiscountedPrice+=cartItem.getDiscountedPrice();
			totalItem+=cartItem.getQuantity();
		}
		cart.setTotalPrice(totalPrice);
		cart.setTotalDiscountedPrice(totalDiscountedPrice);
		cart.setTotalItem(totalItem);
		cart.setDiscounted(totalPrice - totalDiscountedPrice);
		return cartRepository.save(cart);
	}
}
