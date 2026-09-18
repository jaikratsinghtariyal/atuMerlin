package com.arcad.atumerlin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class ParameterDtos {

    private ParameterDtos() {
    }

    public record ParameterRequest(
            @NotBlank @Size(max = 10) String code,
            @NotBlank @Size(max = 10) String subCode,
            @Size(max = 10) String value1,
            @Size(max = 100) String value2,
            @Size(max = 2) String value3,
            Integer value4,
            Integer value5
    ) {
    }

    public record ParameterResponse(
            Long id,
            String code,
            String subCode,
            String value1,
            String value2,
            String value3,
            Integer value4,
            Integer value5
    ) {
    }
}
