package com.arcad.atumerlin.web;

import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.dto.VatDtos.VatRequest;
import com.arcad.atumerlin.dto.VatDtos.VatResponse;
import com.arcad.atumerlin.service.VatService;
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
@RequestMapping("${app.api.base-path}/vat-codes")
public class VatController {

    private final VatService service;

    public VatController(VatService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<VatResponse> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        return service.list(page, size);
    }

    @GetMapping("/{code}")
    public VatResponse get(@PathVariable String code) {
        return service.get(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VatResponse create(@Valid @RequestBody VatRequest request) {
        return service.create(request);
    }

    @PutMapping("/{code}")
    public VatResponse update(@PathVariable String code, @Valid @RequestBody VatRequest request) {
        return service.update(code, request);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable String code) {
        service.delete(code);
        return ResponseEntity.noContent().build();
    }
}
