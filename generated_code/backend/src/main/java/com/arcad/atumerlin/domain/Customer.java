package com.arcad.atumerlin.domain;

import com.arcad.atumerlin.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Customer master file (IBM i physical file FCUST / CUSTOMER.PF).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "customer")
public class Customer extends AuditableEntity {

    /** CUID - numeric customer id (primary key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** CUSTNM - customer name. */
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    /** CUPHONE - phone. */
    @Column(name = "phone", length = 15)
    private String phone;

    /** CUVAT - VAT number. */
    @Column(name = "vat_number", length = 12)
    private String vatNumber;

    /** CUMAIL - e-mail. */
    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "address_line1", length = 50)
    private String addressLine1;

    @Column(name = "address_line2", length = 50)
    private String addressLine2;

    @Column(name = "address_line3", length = 50)
    private String addressLine3;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "city", length = 30)
    private String city;

    /** CUCOUN - country code. */
    @Column(name = "country_code", length = 2)
    private String countryCode;

    /** CULIMCRE - credit limit. */
    @Column(name = "credit_limit", precision = 11, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    /** CUCREDIT - outstanding credit. */
    @Column(name = "credit", precision = 11, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;

    /** CULASTORD - last order date (maintained by the order service, mirroring trigger ORD701). */
    @Column(name = "last_order_date")
    private LocalDate lastOrderDate;
}
