package com.arcad.atumerlin.repository;

import com.arcad.atumerlin.domain.Family;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, String> {

    Page<Family> findByDeletedFalse(Pageable pageable);

    Page<Family> findByDeletedFalseAndDescriptionContainingIgnoreCase(String description, Pageable pageable);
}
