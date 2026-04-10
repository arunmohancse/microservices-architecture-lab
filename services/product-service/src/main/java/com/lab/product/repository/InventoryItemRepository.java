package com.lab.product.repository;

import com.lab.product.domain.InventoryItem;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    @EntityGraph(attributePaths = {"product"})
    Optional<InventoryItem> findByProductId(UUID productId);
}
