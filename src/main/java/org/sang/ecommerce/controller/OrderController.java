package org.sang.ecommerce.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.exception.OrderException;
import org.sang.ecommerce.exception.UserException;
import org.sang.ecommerce.model.Address;
import org.sang.ecommerce.model.Order;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.service.OrderService;
import org.sang.ecommerce.service.UserService;
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
@RequestMapping("/api/orders")
public class OrderController {

	private final OrderService orderService;
	private final UserService userService;

	@PostMapping("/")
	public ResponseEntity<Order> createOrder(
			@RequestBody Address shipAddress,
			Authentication connectedUser
	){
		User user=((User)connectedUser.getPrincipal());
		Order order=orderService.createOrder(user,shipAddress);
		return ResponseEntity.ok(order);
	}

	@GetMapping("/user")
	public ResponseEntity<List<Order>>findOrderHistoryOfUser(
			Authentication connectedUser
	) throws OrderException {
		User user=((User)connectedUser.getPrincipal());
		List<Order>orders=orderService.findOrderHistoryByUserId(user.getId());

		return ResponseEntity.ok(orders);
	}

	@GetMapping("/{orderId}")
	public ResponseEntity< Order> findOrder(@PathVariable Long orderId,
			Authentication connectedUser) throws OrderException, UserException {
		User user=((User)connectedUser.getPrincipal());
		Order order=orderService.findOrderById(orderId);
		return ResponseEntity.ok(order);
	}
}
