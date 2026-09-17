package com.arcad.atumerlin.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

/**
 * Base class carrying the audit + soft-delete columns that every ATU Merlin
 * master file had on the IBM i side (xxCREA / xxMOD / xxMODID / xxDEL).
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private Instant modifiedAt;

    @Column(name = "modified_by", length = 10)
    private String modifiedBy;

    /** Logical delete flag - mirrors the original DLCODE ('X' = deleted). */
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;
}
