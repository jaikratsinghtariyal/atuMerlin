package com.arcad.atumerlin.service;

import com.arcad.atumerlin.common.BusinessRuleException;
import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.common.PageableFactory;
import com.arcad.atumerlin.common.ResourceNotFoundException;
import com.arcad.atumerlin.domain.Vat;
import com.arcad.atumerlin.dto.VatDtos.VatRequest;
import com.arcad.atumerlin.dto.VatDtos.VatResponse;
import com.arcad.atumerlin.repository.VatRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class VatService {

    private final VatRepository repository;
    private final PageableFactory pageableFactory;

    public VatService(VatRepository repository, PageableFactory pageableFactory) {
        this.repository = repository;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public PageResponse<VatResponse> list(Integer page, Integer size) {
        var pageable = pageableFactory.of(page, size, Sort.by("code").ascending());
        return PageResponse.from(repository.findByDeletedFalse(pageable), VatService::toResponse);
    }

    @Transactional(readOnly = true)
    public VatResponse get(String code) {
        return toResponse(find(code));
    }

    /** Resolves the VAT entity for business logic (order pricing). */
    @Transactional(readOnly = true)
    public Vat require(String code) {
        return find(code);
    }

    public VatResponse create(VatRequest request) {
        String code = request.code();
        if (repository.existsById(code)) {
            throw new BusinessRuleException("VAT code already exists: " + code);
        }
        Vat vat = new Vat();
        vat.setCode(code);
        apply(vat, request);
        return toResponse(repository.save(vat));
    }

    public VatResponse update(String code, VatRequest request) {
        Vat vat = find(code);
        apply(vat, request);
        return toResponse(repository.save(vat));
    }

    public void delete(String code) {
        Vat vat = find(code);
        vat.setDeleted(true);
        repository.save(vat);
    }

    private Vat find(String code) {
        Vat vat = repository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("VAT", code));
        if (vat.isDeleted()) {
            throw new ResourceNotFoundException("VAT", code);
        }
        return vat;
    }

    private void apply(Vat vat, VatRequest request) {
        vat.setRate(request.rate());
        vat.setDescription(request.description());
    }

    static VatResponse toResponse(Vat v) {
        return new VatResponse(v.getCode(), v.getRate(), v.getDescription());
    }
}
