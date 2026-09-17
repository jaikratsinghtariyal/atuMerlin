package com.arcad.atumerlin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Customer order detail line (IBM i physical file FDETO / DETORD.PF).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "order_line")
public class OrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderHeader order;

    /** ODLINE - line number within the order. */
    @Column(name = "line", nullable = false)
    private Integer line;

    /** ODARID - article id. */
    @Column(name = "article_id", length = 6, nullable = false)
    private String articleId;

    /** ODQTY - ordered quantity. */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /** ODQTYLIV - delivered quantity. */
    @Column(name = "delivered_quantity", nullable = false)
    private Integer deliveredQuantity = 0;

    /** ODPRICE - unit price. */
    @Column(name = "unit_price", precision = 9, scale = 2, nullable = false)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    /** VAT code applied to this line. */
    @Column(name = "vat_code", length = 1)
    private String vatCode;

    /** ODTOT - line total excluding VAT. */
    @Column(name = "line_net", precision = 13, scale = 2, nullable = false)
    private BigDecimal lineNet = BigDecimal.ZERO;

    /** VAT amount for the line. */
    @Column(name = "line_vat", precision = 13, scale = 2, nullable = false)
    private BigDecimal lineVat = BigDecimal.ZERO;

    /** ODTOTVAT - line total including VAT. */
    @Column(name = "line_gross", precision = 13, scale = 2, nullable = false)
    private BigDecimal lineGross = BigDecimal.ZERO;
}
