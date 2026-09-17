package com.arcad.atumerlin.repository;

import com.arcad.atumerlin.domain.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Page<Customer> findByDeletedFalse(Pageable pageable);

    Page<Customer> findByDeletedFalseAndNameContainingIgnoreCase(String name, Pageable pageable);
}
