package com.arcad.atumerlin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Generic application parameter file (IBM i physical file FPARAM / PARAMETER.PF).
 * The natural key is (code, subCode); a surrogate id is used to keep the REST
 * resource simple.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "parameter", uniqueConstraints = @UniqueConstraint(
        name = "uk_parameter_code_subcode", columnNames = {"code", "sub_code"}))
public class Parameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** PACODE. */
    @Column(name = "code", length = 10, nullable = false)
    private String code;

    /** PASUBCODE. */
    @Column(name = "sub_code", length = 10, nullable = false)
    private String subCode;

    /** PARM1. */
    @Column(name = "value1", length = 10)
    private String value1;

    /** PARM2. */
    @Column(name = "value2", length = 100)
    private String value2;

    /** PARM3. */
    @Column(name = "value3", length = 2)
    private String value3;

    /** PARM4. */
    @Column(name = "value4")
    private Integer value4;

    /** PARM5. */
    @Column(name = "value5")
    private Integer value5;
}
