package org.sang.ecommerce.exception;

public class StripePaymentException extends RuntimeException{
		public StripePaymentException(String message) {
			super(message);
		}

}
