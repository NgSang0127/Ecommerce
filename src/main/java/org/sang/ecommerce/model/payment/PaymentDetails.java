package org.sang.ecommerce.model.payment;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class PaymentDetails {

	private String paymentMethod;

	private String status;

	private String paymentId;

	private String razorPaymentLinkId;

	private String razorPaymentLinkReferenceId;

	private String razorPaymentLinkStatus;

	private String razorPaymentId;
}
