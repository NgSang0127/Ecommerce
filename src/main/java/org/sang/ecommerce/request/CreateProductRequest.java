package org.sang.ecommerce.request;

import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sang.ecommerce.model.Size;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
	private String title;
	private String description;

	private double price;

	private int discountedPrice;

	private int discountedPercent;

	private int quantity;

	private String brand;

	private String color;

	private final Set<Size> sizes=new HashSet<>();

	private String imageUrl;

	private String firstLevelCategory;

	private String secondLevelCategory;

	private String thirdLevelCategory;
}
