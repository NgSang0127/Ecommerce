package org.sang.ecommerce.common;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.sang.ecommerce.model.Product;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
	private List<T> content;
	private int number;
	private int size;
	private long totalElements;
	private int totalPages;
	private boolean first;
	private boolean last;


}
