package org.sang.ecommerce.service.implementation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.constant.OrderStatus;
import org.sang.ecommerce.constant.PaymentStatus;
import org.sang.ecommerce.exception.OrderException;
import org.sang.ecommerce.model.Address;
import org.sang.ecommerce.model.Cart;
import org.sang.ecommerce.model.CartItem;
import org.sang.ecommerce.model.Order;
import org.sang.ecommerce.model.OrderItem;
import org.sang.ecommerce.model.User;
import org.sang.ecommerce.model.payment.PaymentDetails;
import org.sang.ecommerce.repository.AddressRepository;
import org.sang.ecommerce.repository.OrderItemRepository;
import org.sang.ecommerce.repository.OrderRepository;
import org.sang.ecommerce.repository.UserRepository;
import org.sang.ecommerce.service.CartService;
import org.sang.ecommerce.service.OrderService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	private final CartService cartService;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;

	@Override
	public Order createOrder(User user, Address shipAddress) {
		shipAddress.setUser(user);
		Address address=addressRepository.save(shipAddress);
		user.getAddresses().add(address);
		userRepository.save(user);

		Cart cart=cartService.findUserCart(user.getId());
		List<OrderItem> orderItems=new ArrayList<>();
		for (CartItem item: cart.getCartItems()
			 ) {
			OrderItem orderItem=OrderItem.builder()
					.price(item.getPrice())
					.product(item.getProduct())
					.quantity(item.getQuantity())
					.size(item.getSize())
					.userId(item.getUserId())
					.discountedPrice(item.getDiscountedPrice())
					.build();
			OrderItem savedOrderItem=orderItemRepository.save(orderItem);
			orderItems.add(savedOrderItem);
		}
		PaymentDetails paymentDetails = PaymentDetails.builder()
				.status(PaymentStatus.PENDING)
				.build();
		Order createdOrder=Order.builder()
				.user(user)
				.orderItems(orderItems)
				.totalPrice(cart.getTotalPrice())
				.totalDiscountedPrice(cart.getTotalDiscountedPrice())
				.discounted(cart.getDiscounted())
				.totalItem(cart.getTotalItem())
				.shippingAddress(address)
				.orderDate(LocalDateTime.now())
				.orderStatus(OrderStatus.PENDING)
				.paymentDetails(paymentDetails)
				.build();
		Order savedOrder=orderRepository.save(createdOrder);
		for (OrderItem item: orderItems
			 ) {
			item.setOrder(savedOrder);
			orderItemRepository.save(item);
		}
		return savedOrder;
	}

	@Override
	public Order findOrderById(Long orderId) throws OrderException {
		return orderRepository.findById(orderId).orElseThrow(()-> new OrderException("Order not found with id"+orderId));
	}

	@Override
	public List<Order> findOrderHistoryByUserId(Long userId) throws OrderException {
		return orderRepository.findOrderUser(userId);
	}

	@Override
	public Order placeOrder(Long orderId) throws OrderException {
		Order order=findOrderById(orderId);
		order.setOrderStatus(OrderStatus.PLACED);

		return orderRepository.save(order);
	}

	@Override
	public Order confirmedOrder(Long orderId) throws OrderException {
		Order order=findOrderById(orderId);
		order.setOrderStatus(OrderStatus.CONFIRMED);
		return orderRepository.save(order);
	}

	@Override
	public Order shippedOrder(Long orderId) throws OrderException {
		Order order=findOrderById(orderId);
		order.setOrderStatus(OrderStatus.SHIPPED);
		return orderRepository.save(order);
	}

	@Override
	public Order deliveryOrder(Long orderId) throws OrderException {
		Order order=findOrderById(orderId);
		order.setOrderStatus(OrderStatus.DELIVERED);
		return orderRepository.save(order);
	}

	@Override
	public Order canceleOrder(Long orderId) throws OrderException {
		Order order=findOrderById(orderId);
		order.setOrderStatus(OrderStatus.CANCELLED);
		return orderRepository.save(order);

	}

	@Override
	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	@Override
	public void deleteOrder(Long orderId) throws OrderException {
		Order order=findOrderById(orderId);
		orderRepository.deleteById(orderId);

	}
}
