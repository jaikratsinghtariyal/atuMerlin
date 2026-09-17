package com.arcad.atumerlin.repository;

import com.arcad.atumerlin.domain.Country;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, String> {

    Page<Country> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
