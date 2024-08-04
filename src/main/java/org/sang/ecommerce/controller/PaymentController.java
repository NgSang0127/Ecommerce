package org.sang.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.constant.OrderStatus;
import org.sang.ecommerce.constant.PaymentStatus;
import org.sang.ecommerce.exception.OrderException;
import org.sang.ecommerce.exception.StripePaymentException;
import org.sang.ecommerce.exception.UserException;
import org.sang.ecommerce.model.Order;
import org.sang.ecommerce.repository.OrderRepository;
import org.sang.ecommerce.response.ApiResponse;
import org.sang.ecommerce.response.PaymentLinkResponse;
import org.sang.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PaymentController {

	@Value("${stripe.api.key}")
	private String apiKey;

	private final OrderService orderService;
	private final OrderRepository orderRepository;



	@PostMapping("/payments/{orderId}")
	public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable Long orderId)
			throws StripeException, UserException, OrderException {

		Order order = orderService.findOrderById(orderId);
		Stripe.apiKey = apiKey;

		try {
			SessionCreateParams params = SessionCreateParams.builder()
					.addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
					.setMode(SessionCreateParams.Mode.PAYMENT)
					.setSuccessUrl("http://localhost:3000/payment-success?order_id=" + orderId)
					.setCancelUrl("http://localhost:3000/payment-failed")
					.addLineItem(SessionCreateParams.LineItem.builder()
							.setQuantity(1L)
							.setPriceData(SessionCreateParams.LineItem.PriceData.builder()
									.setCurrency("VND")
									.setUnitAmount((long) (order.getTotalPrice() ))
									.setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
											.setName("Order #" + orderId)
											.build())
									.build())
							.build())
					.build();

			Session session = Session.create(params);

			String paymentLinkId = session.getId();
			String paymentLinkUrl = session.getUrl();

			PaymentLinkResponse res = new PaymentLinkResponse(paymentLinkUrl, paymentLinkId);

			order.setOrderId(paymentLinkId);
			orderRepository.save(order);

			return new ResponseEntity<>(res, HttpStatus.ACCEPTED);

		} catch (StripeException e) {
			System.out.println("Error creating payment link: " + e.getMessage());
			throw new StripePaymentException(e.getMessage());
		}
	}

	@GetMapping("/payments")
	public ResponseEntity<ApiResponse> redirect(@RequestParam(name = "payment_id") String paymentId,
			@RequestParam("order_id") Long orderId) throws StripeException, OrderException {
		Stripe.apiKey = apiKey;
		Order order = orderService.findOrderById(orderId);

		try {
			Session session = Session.retrieve(paymentId);

			if ("complete".equals(session.getPaymentStatus())) {
				order.getPaymentDetails().setPaymentId(paymentId);
				order.getPaymentDetails().setStatus(PaymentStatus.COMPLETED);
				order.setOrderStatus(OrderStatus.PLACED);
				orderRepository.save(order);
			}

			ApiResponse res = new ApiResponse("Your order has been placed");
			return new ResponseEntity<>(res, HttpStatus.OK);

		} catch (Exception e) {
			System.out.println("Error processing payment: " + e.getMessage());
			ApiResponse res = new ApiResponse("Payment failed. Please try again.");
			return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
		}
	}
}
