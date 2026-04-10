package com.lab.product.dto;

import java.util.UUID;

public record InventoryResponse(
        UUID productId,
        String productName,
        int quantityOnHand,
        int reservedQuantity,
        int availableQuantity,
        int lowStockThreshold
) {}
