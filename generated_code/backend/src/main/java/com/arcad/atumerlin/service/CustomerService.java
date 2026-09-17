package com.arcad.atumerlin.service;

import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.common.PageableFactory;
import com.arcad.atumerlin.common.ResourceNotFoundException;
import com.arcad.atumerlin.domain.Customer;
import com.arcad.atumerlin.dto.CustomerDtos.CustomerRequest;
import com.arcad.atumerlin.dto.CustomerDtos.CustomerResponse;
import com.arcad.atumerlin.repository.CustomerRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository repository;
    private final PageableFactory pageableFactory;

    public CustomerService(CustomerRepository repository, PageableFactory pageableFactory) {
        this.repository = repository;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public PageResponse<CustomerResponse> list(String search, Integer page, Integer size) {
        var pageable = pageableFactory.of(page, size, Sort.by("name").ascending());
        var result = StringUtils.hasText(search)
                ? repository.findByDeletedFalseAndNameContainingIgnoreCase(search, pageable)
                : repository.findByDeletedFalse(pageable);
        return PageResponse.from(result, CustomerService::toResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        return toResponse(find(id));
    }

    @Transactional(readOnly = true)
    public Customer require(Long id) {
        return find(id);
    }

    public CustomerResponse create(CustomerRequest request) {
        Customer customer = new Customer();
        apply(customer, request);
        return toResponse(repository.save(customer));
    }

    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = find(id);
        apply(customer, request);
        return toResponse(repository.save(customer));
    }

    public void delete(Long id) {
        Customer customer = find(id);
        customer.setDeleted(true);
        repository.save(customer);
    }

    private Customer find(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        if (customer.isDeleted()) {
            throw new ResourceNotFoundException("Customer", id);
        }
        return customer;
    }

    private void apply(Customer c, CustomerRequest r) {
        c.setName(r.name());
        c.setPhone(r.phone());
        c.setVatNumber(r.vatNumber());
        c.setEmail(r.email());
        c.setAddressLine1(r.addressLine1());
        c.setAddressLine2(r.addressLine2());
        c.setAddressLine3(r.addressLine3());
        c.setZipCode(r.zipCode());
        c.setCity(r.city());
        c.setCountryCode(r.countryCode());
        c.setCreditLimit(Optional.ofNullable(r.creditLimit()).orElse(BigDecimal.ZERO));
        c.setCredit(Optional.ofNullable(r.credit()).orElse(BigDecimal.ZERO));
    }

    static CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getPhone(), c.getVatNumber(), c.getEmail(),
                c.getAddressLine1(), c.getAddressLine2(), c.getAddressLine3(),
                c.getZipCode(), c.getCity(), c.getCountryCode(),
                c.getCreditLimit(), c.getCredit(), c.getLastOrderDate());
    }
}
