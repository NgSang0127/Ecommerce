package org.sang.ecommerce.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sang.ecommerce.constant.OrderStatus;
import org.sang.ecommerce.model.payment.PaymentDetails;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = " order")
public class Order {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String orderId;

	@ManyToOne
	private User user;

	@OneToMany(mappedBy = "orderId",cascade = CascadeType.ALL)
	private List<OrderItem> orderItems=new ArrayList<>();

	private LocalDateTime orderDate;

	@CreatedDate
	private LocalDateTime createdDate;

	private LocalDateTime deliveryDate;

	@OneToOne
	private Address shippingAddress;

	@Embedded
	private PaymentDetails paymentDetails=new PaymentDetails();

	private double totalPrice;

	private double totalDiscountedPrice;

	private double discounted;

	private OrderStatus orderStatus;

	private int totalItem;



}
