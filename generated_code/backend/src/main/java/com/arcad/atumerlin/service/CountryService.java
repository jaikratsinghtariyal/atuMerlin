package com.arcad.atumerlin.service;

import com.arcad.atumerlin.common.BusinessRuleException;
import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.common.PageableFactory;
import com.arcad.atumerlin.common.ResourceNotFoundException;
import com.arcad.atumerlin.domain.Country;
import com.arcad.atumerlin.dto.CountryDtos.CountryRequest;
import com.arcad.atumerlin.dto.CountryDtos.CountryResponse;
import com.arcad.atumerlin.repository.CountryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class CountryService {

    private final CountryRepository repository;
    private final PageableFactory pageableFactory;

    public CountryService(CountryRepository repository, PageableFactory pageableFactory) {
        this.repository = repository;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public PageResponse<CountryResponse> list(String search, Integer page, Integer size) {
        var pageable = pageableFactory.of(page, size, Sort.by("name").ascending());
        var result = StringUtils.hasText(search)
                ? repository.findByNameContainingIgnoreCase(search, pageable)
                : repository.findAll(pageable);
        return PageResponse.from(result, CountryService::toResponse);
    }

    @Transactional(readOnly = true)
    public CountryResponse get(String code) {
        return toResponse(find(code));
    }

    public CountryResponse create(CountryRequest request) {
        String code = request.code().toUpperCase();
        if (repository.existsById(code)) {
            throw new BusinessRuleException("Country already exists: " + code);
        }
        Country country = new Country();
        country.setCode(code);
        apply(country, request);
        return toResponse(repository.save(country));
    }

    public CountryResponse update(String code, CountryRequest request) {
        Country country = find(code);
        apply(country, request);
        return toResponse(repository.save(country));
    }

    public void delete(String code) {
        Country country = find(code);
        repository.delete(country);
    }

    private Country find(String code) {
        return repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Country", code));
    }

    private void apply(Country country, CountryRequest request) {
        country.setName(request.name());
        country.setIsoCode(request.isoCode());
    }

    static CountryResponse toResponse(Country c) {
        return new CountryResponse(c.getCode(), c.getName(), c.getIsoCode());
    }
}
