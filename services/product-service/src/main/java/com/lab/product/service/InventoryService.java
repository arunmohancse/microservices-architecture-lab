package com.lab.product.service;

import com.lab.product.dto.InventoryResponse;
import com.lab.product.dto.InventoryUpdateRequest;
import com.lab.product.repository.InventoryItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryItemRepository inventoryItemRepository;

    public InventoryResponse findByProductId(UUID productId) {
        return inventoryItemRepository.findByProductId(productId)
                .map(item -> new InventoryResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantityOnHand(),
                        item.getReservedQuantity(),
                        item.getAvailableQuantity(),
                        item.getLowStockThreshold()))
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + productId));
    }

    @Transactional
    public InventoryResponse update(UUID productId, InventoryUpdateRequest request) {
        var item = inventoryItemRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Inventory not found for product: " + productId));

        item.setQuantityOnHand(request.quantityOnHand());
        if (request.lowStockThreshold() != null) {
            item.setLowStockThreshold(request.lowStockThreshold());
        }

        item = inventoryItemRepository.save(item);

        return new InventoryResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantityOnHand(),
                item.getReservedQuantity(),
                item.getAvailableQuantity(),
                item.getLowStockThreshold());
    }
}
