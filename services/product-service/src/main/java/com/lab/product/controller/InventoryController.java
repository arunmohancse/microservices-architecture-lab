package com.lab.product.controller;

import com.lab.product.dto.InventoryResponse;
import com.lab.product.dto.InventoryUpdateRequest;
import com.lab.product.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // Phase 4 will also add PUT /inventory/{productId}/reserve used by order-service
    @GetMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryResponse findByProductId(@PathVariable UUID productId) {
        return inventoryService.findByProductId(productId);
    }

    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public InventoryResponse update(
            @PathVariable UUID productId,
            @Valid @RequestBody InventoryUpdateRequest request) {
        return inventoryService.update(productId, request);
    }
}
