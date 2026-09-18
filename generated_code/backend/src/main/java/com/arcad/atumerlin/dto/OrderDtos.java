package com.arcad.atumerlin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public final class OrderDtos {

    private OrderDtos() {
    }

    /** Create an order. Unit price and VAT code default from the article when omitted. */
    public record OrderRequest(
            @NotNull Long customerId,
            LocalDate orderDate,
            @NotEmpty @Valid List<OrderLineRequest> lines
    ) {
    }

    public record OrderLineRequest(
            @Size(max = 6) String articleId,
            @NotNull @Positive Integer quantity,
            @PositiveOrZero BigDecimal unitPrice,
            @Size(max = 1) String vatCode
    ) {
    }

    /** Update delivery/close dates and optionally mark full delivery. */
    public record OrderStatusRequest(
            LocalDate deliveryDate,
            LocalDate closeDate,
            Boolean markFullyDelivered
    ) {
    }

    public record OrderLineResponse(
            Integer line,
            String articleId,
            String articleDescription,
            Integer quantity,
            Integer deliveredQuantity,
            BigDecimal unitPrice,
            String vatCode,
            BigDecimal lineNet,
            BigDecimal lineVat,
            BigDecimal lineGross
    ) {
    }

    public record OrderResponse(
            Long id,
            Integer year,
            Long customerId,
            String customerName,
            LocalDate orderDate,
            LocalDate deliveryDate,
            LocalDate closeDate,
            BigDecimal totalNet,
            BigDecimal totalVat,
            BigDecimal totalGross,
            List<OrderLineResponse> lines
    ) {
    }
}
