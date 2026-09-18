package com.arcad.atumerlin.service;

import com.arcad.atumerlin.common.BusinessRuleException;
import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.common.PageableFactory;
import com.arcad.atumerlin.common.ResourceNotFoundException;
import com.arcad.atumerlin.domain.Family;
import com.arcad.atumerlin.dto.FamilyDtos.FamilyRequest;
import com.arcad.atumerlin.dto.FamilyDtos.FamilyResponse;
import com.arcad.atumerlin.repository.FamilyRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class FamilyService {

    private final FamilyRepository repository;
    private final PageableFactory pageableFactory;

    public FamilyService(FamilyRepository repository, PageableFactory pageableFactory) {
        this.repository = repository;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public PageResponse<FamilyResponse> list(String search, Integer page, Integer size) {
        var pageable = pageableFactory.of(page, size, Sort.by("id").ascending());
        var result = StringUtils.hasText(search)
                ? repository.findByDeletedFalseAndDescriptionContainingIgnoreCase(search, pageable)
                : repository.findByDeletedFalse(pageable);
        return PageResponse.from(result, FamilyService::toResponse);
    }

    @Transactional(readOnly = true)
    public FamilyResponse get(String id) {
        return toResponse(find(id));
    }

    public FamilyResponse create(FamilyRequest request) {
        String id = request.id().toUpperCase();
        if (repository.existsById(id)) {
            throw new BusinessRuleException("Family already exists: " + id);
        }
        Family family = new Family();
        family.setId(id);
        apply(family, request);
        return toResponse(repository.save(family));
    }

    public FamilyResponse update(String id, FamilyRequest request) {
        Family family = find(id);
        apply(family, request);
        return toResponse(repository.save(family));
    }

    public void delete(String id) {
        Family family = find(id);
        family.setDeleted(true);
        repository.save(family);
    }

    private Family find(String id) {
        Family family = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Family", id));
        if (family.isDeleted()) {
            throw new ResourceNotFoundException("Family", id);
        }
        return family;
    }

    private void apply(Family family, FamilyRequest request) {
        family.setDescription(request.description());
        family.setDefaultVatCode(request.defaultVatCode());
    }

    static FamilyResponse toResponse(Family f) {
        return new FamilyResponse(f.getId(), f.getDescription(), f.getDefaultVatCode());
    }
}
