package com.possystem.catalog.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        String barcode,
        Boolean isActive,
        CategoryDto category,
        BrandDto brand,
        LocalDateTime createdAt
) {}
