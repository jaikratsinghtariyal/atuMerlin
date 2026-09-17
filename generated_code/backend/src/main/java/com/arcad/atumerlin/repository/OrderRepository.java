package com.arcad.atumerlin.repository;

import com.arcad.atumerlin.domain.OrderHeader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderHeader, Long> {

    Page<OrderHeader> findByCustomerId(Long customerId, Pageable pageable);
}
