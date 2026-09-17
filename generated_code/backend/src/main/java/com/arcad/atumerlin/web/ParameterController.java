package com.arcad.atumerlin.web;

import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.dto.ParameterDtos.ParameterRequest;
import com.arcad.atumerlin.dto.ParameterDtos.ParameterResponse;
import com.arcad.atumerlin.service.ParameterService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.base-path}/parameters")
public class ParameterController {

    private final ParameterService service;

    public ParameterController(ParameterService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<ParameterResponse> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return service.list(search, page, size);
    }

    @GetMapping("/{id}")
    public ParameterResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParameterResponse create(@Valid @RequestBody ParameterRequest request) {
        return service.create(request);
    }

    @PutMapping("/{id}")
    public ParameterResponse update(@PathVariable Long id, @Valid @RequestBody ParameterRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
