package org.sang.ecommerce.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequest {
	private Long productId;
	private String size;
	private int quantity;
	private Double price;
}
