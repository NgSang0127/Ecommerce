package org.sang.ecommerce.service.implementation;

import io.jsonwebtoken.lang.Strings;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.sang.ecommerce.common.PageResponse;
import org.sang.ecommerce.exception.ProductException;
import org.sang.ecommerce.model.Category;
import org.sang.ecommerce.model.Product;
import org.sang.ecommerce.repository.CategoryRepository;
import org.sang.ecommerce.repository.ProductRepository;
import org.sang.ecommerce.request.CreateProductRequest;
import org.sang.ecommerce.service.ProductService;
import org.sang.ecommerce.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	@Override
	public Product createProduct(CreateProductRequest request) {
		Category firstLevel = categoryRepository.findByName(request.getFirstLevelCategory());
		if (firstLevel == null) {
			var firstLevelCategory = Category.builder()
					.name(request.getFirstLevelCategory())
					.level(1)
					.build();
			firstLevel = categoryRepository.save(firstLevelCategory);
		}
		Category secondLevel = categoryRepository.findByNameAndParent(request.getSecondLevelCategory(),
				firstLevel.getName());
		if (secondLevel == null) {
			Category secondLevelCategory = Category.builder()
					.name(request.getSecondLevelCategory())
					.parentCategory(firstLevel)
					.level(2)
					.build();
			secondLevel = categoryRepository.save(secondLevelCategory);
		}

		Category thirdLevel = categoryRepository.findByNameAndParent(request.getThirdLevelCategory(),
				secondLevel.getName());
		if (thirdLevel == null) {
			Category thirdLevelCategory = Category.builder()
					.name(request.getThirdLevelCategory())
					.parentCategory(secondLevel)
					.level(3)
					.build();
			thirdLevel = categoryRepository.save(thirdLevelCategory);
		}
		Product product = Product.builder()
				.title(request.getTitle())
				.color(request.getColor())
				.description(request.getDescription())
				.discountedPrice(request.getDiscountedPrice())
				.discountPercent(request.getDiscountedPercent())
				.imageUrl(request.getImageUrl())
				.brand(request.getBrand())
				.price(request.getPrice())
				.sizes(request.getSizes())
				.quantity(request.getQuantity())
				.category(thirdLevel)
				.build();
		return productRepository.save(product);

	}

	@Override
	public String deleteProduct(Long productId) throws ProductException {
		Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException("Product not "
				+ "found with id:" + productId));
		product.getSizes().clear();
		productRepository.delete(product);
		return ("Product deleted successful with id" + productId);
	}

	@Override
	public Product updateProduct(Long productId, Product productReq) throws ProductException {
		Product product=productRepository.findById(productId).orElseThrow(()-> new ProductException("Product not "
				+ "found with id"+productId));
		if(productReq.getQuantity() != 0){
			product.setQuantity(productReq.getQuantity());
		}
		if(productReq.getDescription() != null){
			product.setDescription(productReq.getDescription());
		}
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public List<Product> searchProduct(String keyword) throws ProductException {
		return productRepository.findAll(ProductSpecification.searchProduct(keyword));
	}

	@Override
	public Product findProductById(Long id) throws ProductException {
		return productRepository.findById(id).orElseThrow(()-> new ProductException("Product not found with id :"+id));
	}

	@Override
	public List<Product> findProductByCategory(String category) throws ProductException {
		return productRepository.findByCategory(category);
	}

	@Override
	public PageResponse<Product> getAllProduct(String category, List<String> colors, List<String> sizes, Double minPrice,
			Double maxPrice, Integer minDiscount, String sort, String stock, Integer pageNumber, Integer pageSize) {
		Pageable pageable= PageRequest.of(pageNumber,pageSize);
		Specification<Product> spec=Specification.where(ProductSpecification.hasCategory(category))
				.and(ProductSpecification.hasPriceBetween(minPrice,maxPrice))
				.and(ProductSpecification.hasMinDiscount(minDiscount))
				.and(ProductSpecification.applySort(sort));
		List<Product> products=productRepository.findAll(spec);

		if(!colors.isEmpty()){
			products=products.stream()
					.filter(p -> colors.stream().anyMatch(c->c.equalsIgnoreCase(p.getColor())))
					.toList();
		}
		if(stock != null){
			if(stock.equals("in_stock")){
				products=products.stream()
						.filter(p->p.getQuantity() >0)
						.toList();
			} else if (stock.equals("out_of_stock")) {
				products=products.stream()
						.filter(p->p.getQuantity() <1)
						.toList();
			}

		}
		Page<Product>filledProduct=productRepository.filterProducts(spec,pageable);

		return new PageResponse<>(
				products,
				filledProduct.getNumber(),
				filledProduct.getSize(),
				filledProduct.getTotalElements(),
				filledProduct.getTotalPages(),
				filledProduct.isFirst(),
				filledProduct.isLast()
		);
	}
}
