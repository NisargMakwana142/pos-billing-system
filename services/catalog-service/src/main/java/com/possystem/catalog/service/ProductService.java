package com.possystem.catalog.service;

import com.possystem.catalog.dto.BrandDto;
import com.possystem.catalog.dto.CategoryDto;
import com.possystem.catalog.dto.ProductRequest;
import com.possystem.catalog.dto.ProductResponse;
import com.possystem.catalog.entity.Brand;
import com.possystem.catalog.entity.Category;
import com.possystem.catalog.entity.Product;
import com.possystem.catalog.exception.ResourceNotFoundException;
import com.possystem.catalog.repository.BrandRepository;
import com.possystem.catalog.repository.CategoryRepository;
import com.possystem.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;

    public List<ProductResponse> getAllProducts(){
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ProductResponse createProduct(ProductRequest request){

        Category category = null;
        if (request.categoryId() != null){
            category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category with this id not found "+request.categoryId()));
        }

        Brand brand =null;
        if (request.brandId() != null){
            brand = brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand with this id not found "+request.brandId()));
        }

        Product product = Product.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .barcode(request.barcode())
                .isActive(true)
                .category(category)
                .brand(brand)
                .build();

        Product savedProduct = productRepository.save(product);

        return mapToResponse(savedProduct);

    }

    public ProductResponse getProductById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with this id: "+id));

        return mapToResponse(product);
    }

    public ProductResponse updateProduct(Long id, ProductRequest request){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with this id: "+id));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setBarcode(request.barcode());

        //if new category is provided in update request
        if (request.categoryId() != null){
            Category category = categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with this id: "+request.categoryId()));
            product.setCategory(category);
        }

        //if new brand is provided in update request
        if (request.brandId() != null){
            Brand brand = brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand not found with this id: "+request.brandId()));
            product.setBrand(brand);
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponse(updatedProduct);
    }

    public void deleteProduct(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with this id: "+id));

        product.setIsActive(false);
        productRepository.save(product);
    }


    private ProductResponse mapToResponse(Product p){
        CategoryDto catDto = p.getCategory() != null
                ? new CategoryDto(p.getCategory().getId(), p.getCategory().getName(), p.getCategory().getDescription())
                : null;

        BrandDto brandDto = p.getBrand() != null
                ? new BrandDto(p.getBrand().getId(), p.getBrand().getName())
                : null;

        return new ProductResponse(
                p.getId(),p.getName(),p.getDescription(),p.getPrice(),p.getStockQuantity()
                ,p.getBarcode(),p.getIsActive(),catDto,brandDto,p.getCreatedAt()
        );
    }

}
