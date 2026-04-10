package com.lab.product.service;

import com.lab.product.domain.InventoryItem;
import com.lab.product.domain.Product;
import com.lab.product.dto.ProductRequest;
import com.lab.product.dto.ProductResponse;
import com.lab.product.repository.CategoryRepository;
import com.lab.product.repository.InventoryItemRepository;
import com.lab.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public Page<ProductResponse> findAll(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(this::toResponse);
    }

    public Page<ProductResponse> findByCategory(String slug, Pageable pageable) {
        return productRepository.findByCategorySlugAndActiveTrue(slug, pageable).map(this::toResponse);
    }

    public ProductResponse findById(UUID id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.categoryId()));

        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);
        product.setImageUrl(request.imageUrl());
        product = productRepository.save(product);

        // Auto-create an empty inventory record for this product
        InventoryItem inventory = new InventoryItem();
        inventory.setProduct(product);
        inventoryItemRepository.save(inventory);

        return toResponse(product);
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        var category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found: " + request.categoryId()));

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);
        product.setImageUrl(request.imageUrl());

        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: " + id));
        product.setActive(false); // soft delete — preserves order history references
        productRepository.save(product);
    }

    private ProductResponse toResponse(Product p) {
        int available = p.getInventoryItem() != null
                ? p.getInventoryItem().getAvailableQuantity()
                : 0;

        return new ProductResponse(
                p.getId(),
                p.getName(),
                p.getDescription(),
                p.getPrice(),
                p.getCategory().getId(),
                p.getCategory().getName(),
                p.getImageUrl(),
                p.isActive(),
                available
        );
    }
}
