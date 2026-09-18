package com.arcad.atumerlin.service;

import com.arcad.atumerlin.common.BusinessRuleException;
import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.common.PageableFactory;
import com.arcad.atumerlin.common.ResourceNotFoundException;
import com.arcad.atumerlin.domain.Parameter;
import com.arcad.atumerlin.dto.ParameterDtos.ParameterRequest;
import com.arcad.atumerlin.dto.ParameterDtos.ParameterResponse;
import com.arcad.atumerlin.repository.ParameterRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class ParameterService {

    private final ParameterRepository repository;
    private final PageableFactory pageableFactory;

    public ParameterService(ParameterRepository repository, PageableFactory pageableFactory) {
        this.repository = repository;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public PageResponse<ParameterResponse> list(String search, Integer page, Integer size) {
        var pageable = pageableFactory.of(page, size, Sort.by("code").ascending().and(Sort.by("subCode")));
        var result = StringUtils.hasText(search)
                ? repository.findByCodeContainingIgnoreCase(search, pageable)
                : repository.findAll(pageable);
        return PageResponse.from(result, ParameterService::toResponse);
    }

    @Transactional(readOnly = true)
    public ParameterResponse get(Long id) {
        return toResponse(find(id));
    }

    public ParameterResponse create(ParameterRequest request) {
        repository.findByCodeAndSubCode(request.code(), request.subCode()).ifPresent(p -> {
            throw new BusinessRuleException(
                    "Parameter already exists: %s/%s".formatted(request.code(), request.subCode()));
        });
        Parameter parameter = new Parameter();
        apply(parameter, request);
        return toResponse(repository.save(parameter));
    }

    public ParameterResponse update(Long id, ParameterRequest request) {
        Parameter parameter = find(id);
        apply(parameter, request);
        return toResponse(repository.save(parameter));
    }

    public void delete(Long id) {
        repository.delete(find(id));
    }

    private Parameter find(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parameter", id));
    }

    private void apply(Parameter p, ParameterRequest r) {
        p.setCode(r.code());
        p.setSubCode(r.subCode());
        p.setValue1(r.value1());
        p.setValue2(r.value2());
        p.setValue3(r.value3());
        p.setValue4(r.value4());
        p.setValue5(r.value5());
    }

    static ParameterResponse toResponse(Parameter p) {
        return new ParameterResponse(
                p.getId(), p.getCode(), p.getSubCode(), p.getValue1(), p.getValue2(),
                p.getValue3(), p.getValue4(), p.getValue5());
    }
}
