package com.arcad.atumerlin.domain;

import com.arcad.atumerlin.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * VAT code / rate reference file (IBM i physical file FVAT / VATDEF.PF).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "vat")
public class Vat extends AuditableEntity {

    /** VATCODE - 1 char code (primary key). */
    @Id
    @Column(name = "code", length = 1, nullable = false)
    private String code;

    /** VATRATE - percentage rate, e.g. 21.00. */
    @Column(name = "rate", precision = 6, scale = 2, nullable = false)
    private BigDecimal rate;

    /** VATDESC - description. */
    @Column(name = "description", length = 20)
    private String description;
}
