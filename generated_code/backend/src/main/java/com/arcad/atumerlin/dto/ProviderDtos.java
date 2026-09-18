package com.arcad.atumerlin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ProviderDtos {

    private ProviderDtos() {
    }

    public record ProviderRequest(
            @NotBlank @Size(max = 30) String name,
            @Size(max = 30) String contact,
            @Size(max = 15) String phone,
            @Size(max = 12) String vatNumber,
            @Email @Size(max = 50) String email,
            @Size(max = 50) String addressLine1,
            @Size(max = 50) String addressLine2,
            @Size(max = 50) String addressLine3,
            @Size(max = 10) String zipCode,
            @Size(max = 30) String city,
            @Size(max = 2) String countryCode
    ) {
    }

    public record ProviderResponse(
            Long id,
            String name,
            String contact,
            String phone,
            String vatNumber,
            String email,
            String addressLine1,
            String addressLine2,
            String addressLine3,
            String zipCode,
            String city,
            String countryCode
    ) {
    }
}
