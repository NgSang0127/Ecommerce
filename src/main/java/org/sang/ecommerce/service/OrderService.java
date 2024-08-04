package org.sang.ecommerce.service;

import java.util.List;
import org.sang.ecommerce.exception.OrderException;
import org.sang.ecommerce.model.Address;
import org.sang.ecommerce.model.Order;
import org.sang.ecommerce.model.User;

public interface OrderService {
	Order createOrder(User user , Address shipAddress);

	Order findOrderById(Long orderId) throws OrderException;

	List<Order> findOrderHistoryByUserId(Long userId) throws OrderException;

	Order placeOrder(Long orderId) throws OrderException;

	Order confirmedOrder(Long orderId) throws OrderException;

	Order shippedOrder(Long orderId) throws OrderException;

	Order deliveryOrder(Long orderId) throws OrderException;

	Order canceleOrder(Long orderId) throws OrderException;

	List<Order> getAllOrders();

	void deleteOrder(Long orderId) throws OrderException;

}
