package com.arcad.atumerlin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class FamilyDtos {

    private FamilyDtos() {
    }

    public record FamilyRequest(
            @NotBlank @Size(max = 3) String id,
            @NotBlank @Size(max = 50) String description,
            @Size(max = 1) String defaultVatCode
    ) {
    }

    public record FamilyResponse(
            String id,
            String description,
            String defaultVatCode
    ) {
    }
}
