package com.arcad.atumerlin.domain;

import com.arcad.atumerlin.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Article master file (IBM i physical file FARTI / ARTICLE.PF, plus the free-text
 * ARTIINF description table).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "article")
public class Article extends AuditableEntity {

    /** ARID - 6 char article id (primary key). */
    @Id
    @Column(name = "id", length = 6, nullable = false)
    private String id;

    /** ARDESC - description. */
    @Column(name = "description", length = 50, nullable = false)
    private String description;

    /** ARSALEPR - reference sale price. */
    @Column(name = "sale_price", precision = 9, scale = 2)
    private BigDecimal salePrice = BigDecimal.ZERO;

    /** ARWHSPR - warehouse/stock price. */
    @Column(name = "warehouse_price", precision = 9, scale = 2)
    private BigDecimal warehousePrice = BigDecimal.ZERO;

    /** ARTIFA - family id. */
    @Column(name = "family_id", length = 3)
    private String familyId;

    /** ARSTOCK - current stock. */
    @Column(name = "stock")
    private Integer stock = 0;

    /** ARMINQTY - minimum stock. */
    @Column(name = "min_stock")
    private Integer minStock = 0;

    /** ARCUSQTY - customer order quantity. */
    @Column(name = "customer_order_qty")
    private Integer customerOrderQty = 0;

    /** ARPURQTY - purchase order quantity. */
    @Column(name = "purchase_order_qty")
    private Integer purchaseOrderQty = 0;

    /** ARVATCD - VAT code. */
    @Column(name = "vat_code", length = 1)
    private String vatCode;

    /** ARTINF - free text article information (from ARTIINF). */
    @Lob
    @Column(name = "information")
    private String information;
}
