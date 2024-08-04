package org.sang.ecommerce.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.common.PageResponse;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Product;
import org.sang.ecommerce.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {
	private final ProductService productService;


	@GetMapping
	public ResponseEntity<PageResponse<Product>> findProductByCategoryHandler(@RequestParam String category,
			@RequestParam List<String> color,@RequestParam List<String> size,@RequestParam Double minPrice,
			@RequestParam Double maxPrice, @RequestParam Integer minDiscount, @RequestParam String sort,
			@RequestParam String stock, @RequestParam Integer pageNumber,@RequestParam Integer pageSize){


		PageResponse<Product> res= productService.getAllProduct(category, color, size, minPrice, maxPrice, minDiscount, sort
				,stock,pageNumber,pageSize);
		return ResponseEntity.ok(res);

	}



	@GetMapping("/id/{productId}")
	public ResponseEntity<Product> findProductById(@PathVariable Long productId) throws ProductException {

		Product product=productService.findProductById(productId);

		return ResponseEntity.ok(product);
	}

	@GetMapping("/search")
	public ResponseEntity<List<Product>> searchProductHandler(@RequestParam String keyword) throws ProductException {

		List<Product> products=productService.searchProduct(keyword);

		return ResponseEntity.ok(products);

	}
}
