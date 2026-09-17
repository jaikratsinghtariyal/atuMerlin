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

/**
 * Provider (supplier) master file (IBM i physical file FPROV / PROVIDER.PF).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "provider")
public class Provider extends AuditableEntity {

    /** PRID - numeric provider id (primary key). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** PROVNM - provider name. */
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    /** PRCONT - contact person. */
    @Column(name = "contact", length = 30)
    private String contact;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "vat_number", length = 12)
    private String vatNumber;

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

    @Column(name = "country_code", length = 2)
    private String countryCode;
}
