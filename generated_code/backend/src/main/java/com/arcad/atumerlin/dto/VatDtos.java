package com.arcad.atumerlin.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public final class VatDtos {

    private VatDtos() {
    }

    public record VatRequest(
            @NotBlank @Size(max = 1) String code,
            @NotNull @DecimalMin("0.0") BigDecimal rate,
            @Size(max = 20) String description
    ) {
    }

    public record VatResponse(
            String code,
            BigDecimal rate,
            String description
    ) {
    }
}
