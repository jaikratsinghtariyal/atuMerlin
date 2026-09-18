package com.arcad.atumerlin.repository;

import com.arcad.atumerlin.domain.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Page<Provider> findByDeletedFalse(Pageable pageable);

    Page<Provider> findByDeletedFalseAndNameContainingIgnoreCase(String name, Pageable pageable);
}
