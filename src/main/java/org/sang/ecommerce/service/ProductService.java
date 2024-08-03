package org.sang.ecommerce.service;

import java.util.List;
import org.sang.ecommerce.common.PageResponse;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Product;
import org.sang.ecommerce.request.CreateProductRequest;
import org.springframework.data.domain.Page;

public interface ProductService {

	Product createProduct(CreateProductRequest request);

	String deleteProduct(Long productId) throws ProductException;

	Product updateProduct(Long productId,Product productReq) throws ProductException;

	Product findProductById(Long id) throws ProductException;

	List<Product> getAllProducts();

	List<Product> searchProduct(String keyword) throws ProductException;

	List<Product> findProductByCategory(String category) throws ProductException;

	PageResponse<Product> getAllProduct(String category, List<String>colors,List<String> sizes,Double minPrice,
			Double maxPrice,Integer minDiscount,String sort,String stock,Integer pageNumber,Integer pageSize);



}
