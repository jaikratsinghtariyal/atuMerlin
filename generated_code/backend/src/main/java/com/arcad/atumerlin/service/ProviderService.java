package com.arcad.atumerlin.service;

import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.common.PageableFactory;
import com.arcad.atumerlin.common.ResourceNotFoundException;
import com.arcad.atumerlin.domain.Provider;
import com.arcad.atumerlin.dto.ProviderDtos.ProviderRequest;
import com.arcad.atumerlin.dto.ProviderDtos.ProviderResponse;
import com.arcad.atumerlin.repository.ProviderRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class ProviderService {

    private final ProviderRepository repository;
    private final PageableFactory pageableFactory;

    public ProviderService(ProviderRepository repository, PageableFactory pageableFactory) {
        this.repository = repository;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProviderResponse> list(String search, Integer page, Integer size) {
        var pageable = pageableFactory.of(page, size, Sort.by("name").ascending());
        var result = StringUtils.hasText(search)
                ? repository.findByDeletedFalseAndNameContainingIgnoreCase(search, pageable)
                : repository.findByDeletedFalse(pageable);
        return PageResponse.from(result, ProviderService::toResponse);
    }

    @Transactional(readOnly = true)
    public ProviderResponse get(Long id) {
        return toResponse(find(id));
    }

    public ProviderResponse create(ProviderRequest request) {
        Provider provider = new Provider();
        apply(provider, request);
        return toResponse(repository.save(provider));
    }

    public ProviderResponse update(Long id, ProviderRequest request) {
        Provider provider = find(id);
        apply(provider, request);
        return toResponse(repository.save(provider));
    }

    public void delete(Long id) {
        Provider provider = find(id);
        provider.setDeleted(true);
        repository.save(provider);
    }

    private Provider find(Long id) {
        Provider provider = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", id));
        if (provider.isDeleted()) {
            throw new ResourceNotFoundException("Provider", id);
        }
        return provider;
    }

    private void apply(Provider p, ProviderRequest r) {
        p.setName(r.name());
        p.setContact(r.contact());
        p.setPhone(r.phone());
        p.setVatNumber(r.vatNumber());
        p.setEmail(r.email());
        p.setAddressLine1(r.addressLine1());
        p.setAddressLine2(r.addressLine2());
        p.setAddressLine3(r.addressLine3());
        p.setZipCode(r.zipCode());
        p.setCity(r.city());
        p.setCountryCode(r.countryCode());
    }

    static ProviderResponse toResponse(Provider p) {
        return new ProviderResponse(
                p.getId(), p.getName(), p.getContact(), p.getPhone(), p.getVatNumber(),
                p.getEmail(), p.getAddressLine1(), p.getAddressLine2(), p.getAddressLine3(),
                p.getZipCode(), p.getCity(), p.getCountryCode());
    }
}
