package org.sang.ecommerce.repository;

import org.sang.ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

	Cart findByUserId(Long userId);
}
