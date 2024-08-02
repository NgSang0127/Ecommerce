package org.sang.ecommerce.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class PaymentInformation {

	private String cardholderName;

	private String cartNumber;

	private LocalDate validUntil;

	private String cvv;
}
