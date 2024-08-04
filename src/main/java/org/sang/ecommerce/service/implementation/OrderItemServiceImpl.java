package org.sang.ecommerce.service.implementation;

import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.model.OrderItem;
import org.sang.ecommerce.repository.OrderItemRepository;
import org.sang.ecommerce.service.OrderItemService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {
	private final OrderItemRepository orderItemRepository;

	@Override
	public OrderItem createOrderItem(OrderItem orderItem) {

		return orderItemRepository.save(orderItem);
	}
}
