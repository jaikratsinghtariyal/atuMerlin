package com.arcad.atumerlin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Country reference file (IBM i physical file FCOUN / COUNTRY.PF).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "country")
public class Country {

    /** COID - 2 char country code (primary key). */
    @Id
    @Column(name = "code", length = 2, nullable = false)
    private String code;

    /** COUNTR - country name. */
    @Column(name = "name", length = 30, nullable = false)
    private String name;

    /** COISO - 3 char ISO code. */
    @Column(name = "iso_code", length = 3)
    private String isoCode;
}
