package com.arcad.atumerlin.domain;

import com.arcad.atumerlin.common.AuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Article family reference file (IBM i physical file FFAMI / FAMILLY.PF).
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "family")
public class Family extends AuditableEntity {

    /** FAID - 3 char family id (primary key). */
    @Id
    @Column(name = "id", length = 3, nullable = false)
    private String id;

    /** FADESC - family description. */
    @Column(name = "description", length = 50, nullable = false)
    private String description;

    /** FAVATCD - default VAT code for the family. */
    @Column(name = "default_vat_code", length = 1)
    private String defaultVatCode;
}
