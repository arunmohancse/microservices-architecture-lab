package com.lab.product.repository;

import com.lab.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {

    // Fetch category + inventoryItem in one query to avoid N+1
    @EntityGraph(attributePaths = {"category", "inventoryItem"})
    Page<Product> findByActiveTrue(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "inventoryItem"})
    Page<Product> findByCategorySlugAndActiveTrue(String slug, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "inventoryItem"})
    Optional<Product> findByIdAndActiveTrue(UUID id);
}
