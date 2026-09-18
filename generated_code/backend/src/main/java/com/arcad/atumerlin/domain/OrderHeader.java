package com.arcad.atumerlin.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer order header (IBM i physical file FORDE / ORDER.PF).
 * Money totals are derived from the order lines.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class OrderHeader {

    /** ORID - order number (primary key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** ORYEAR - order year. */
    @Column(name = "order_year", nullable = false)
    private Integer year;

    /** ORCUID - customer id. */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /** ORDATE - order date. */
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /** ORDATDEL - delivery date. */
    @Column(name = "delivery_date")
    private LocalDate deliveryDate;

    /** ORDATCLO - close date. */
    @Column(name = "close_date")
    private LocalDate closeDate;

    @Column(name = "total_net", precision = 13, scale = 2, nullable = false)
    private BigDecimal totalNet = BigDecimal.ZERO;

    @Column(name = "total_vat", precision = 13, scale = 2, nullable = false)
    private BigDecimal totalVat = BigDecimal.ZERO;

    @Column(name = "total_gross", precision = 13, scale = 2, nullable = false)
    private BigDecimal totalGross = BigDecimal.ZERO;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("line ASC")
    private List<OrderLine> lines = new ArrayList<>();

    public void addLine(OrderLine line) {
        line.setOrder(this);
        this.lines.add(line);
    }
}
