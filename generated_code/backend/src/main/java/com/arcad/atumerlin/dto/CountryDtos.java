package com.arcad.atumerlin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class CountryDtos {

    private CountryDtos() {
    }

    public record CountryRequest(
            @NotBlank @Size(max = 2) String code,
            @NotBlank @Size(max = 30) String name,
            @Size(max = 3) String isoCode
    ) {
    }

    public record CountryResponse(
            String code,
            String name,
            String isoCode
    ) {
    }
}
