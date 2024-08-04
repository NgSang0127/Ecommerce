package org.sang.ecommerce.repository;

import org.sang.ecommerce.model.Cart;
import org.sang.ecommerce.model.CartItem;
import org.sang.ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

	@Query("""
			SELECT cartItems
			FROM CartItem cartItems
			WHERE cartItems.cart=:cart
			AND cartItems.product=:product
			AND cartItems.size=:size
			AND cartItems.userId=:userId
			""")
	CartItem isCartItemExists(
			@Param("cart") Cart cart,
			@Param("product") Product product,
			@Param("size") String size,
			@Param("userId") Long userId);
}
