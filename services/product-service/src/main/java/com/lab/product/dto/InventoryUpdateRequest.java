package com.lab.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record InventoryUpdateRequest(
        @NotNull @Min(0) Integer quantityOnHand,
        @Min(1) Integer lowStockThreshold   // null = keep existing value
) {}
