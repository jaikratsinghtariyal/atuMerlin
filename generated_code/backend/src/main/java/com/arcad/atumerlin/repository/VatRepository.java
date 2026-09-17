package com.arcad.atumerlin.repository;

import com.arcad.atumerlin.domain.Vat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VatRepository extends JpaRepository<Vat, String> {

    Page<Vat> findByDeletedFalse(Pageable pageable);
}
