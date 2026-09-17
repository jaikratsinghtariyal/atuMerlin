package com.arcad.atumerlin.service;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * VAT arithmetic migrated from the RPG service program VAT300 (procedure ClcVAT).
 *
 * <p>Original logic: {@code tot = (net * vatrate) / 100} returned as a packed
 * 9,2 value via {@code %dech(tot:9:2)} (half-up rounding).</p>
 */
@Component
public class VatCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /** VAT amount for a net value and a percentage rate, rounded to 2 decimals (half-up). */
    public BigDecimal vatAmount(BigDecimal net, BigDecimal ratePercent) {
        if (net == null || ratePercent == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return net.multiply(ratePercent)
                .divide(HUNDRED, 2, RoundingMode.HALF_UP);
    }
}
