package com.arcad.atumerlin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public final class ArticleDtos {

    private ArticleDtos() {
    }

    public record ArticleRequest(
            @NotBlank @Size(max = 6) String id,
            @NotBlank @Size(max = 50) String description,
            @PositiveOrZero BigDecimal salePrice,
            @PositiveOrZero BigDecimal warehousePrice,
            @Size(max = 3) String familyId,
            @PositiveOrZero Integer stock,
            @PositiveOrZero Integer minStock,
            @PositiveOrZero Integer customerOrderQty,
            @PositiveOrZero Integer purchaseOrderQty,
            @Size(max = 1) String vatCode,
            String information
    ) {
    }

    public record ArticleResponse(
            String id,
            String description,
            BigDecimal salePrice,
            BigDecimal warehousePrice,
            String familyId,
            Integer stock,
            Integer minStock,
            Integer customerOrderQty,
            Integer purchaseOrderQty,
            String vatCode,
            String information
    ) {
    }
}
