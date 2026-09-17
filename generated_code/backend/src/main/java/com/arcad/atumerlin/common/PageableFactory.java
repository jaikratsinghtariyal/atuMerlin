package com.arcad.atumerlin.common;

import com.arcad.atumerlin.config.AppProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

/**
 * Builds {@link Pageable} instances while enforcing the configurable default and
 * maximum page sizes from {@link AppProperties}.
 */
@Component
public class PageableFactory {

    private final AppProperties.Pagination pagination;

    public PageableFactory(AppProperties properties) {
        this.pagination = properties.pagination();
    }

    public Pageable of(Integer page, Integer size, Sort sort) {
        int resolvedPage = (page == null || page < 0) ? 0 : page;
        int resolvedSize = (size == null || size <= 0) ? pagination.defaultPageSize() : size;
        if (resolvedSize > pagination.maxPageSize()) {
            resolvedSize = pagination.maxPageSize();
        }
        return PageRequest.of(resolvedPage, resolvedSize, sort);
    }
}
