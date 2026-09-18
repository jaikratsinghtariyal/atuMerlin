package com.arcad.atumerlin.repository;

import com.arcad.atumerlin.domain.Parameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParameterRepository extends JpaRepository<Parameter, Long> {

    Page<Parameter> findByCodeContainingIgnoreCase(String code, Pageable pageable);

    Optional<Parameter> findByCodeAndSubCode(String code, String subCode);
}
