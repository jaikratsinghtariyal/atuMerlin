package com.arcad.atumerlin.service;

import com.arcad.atumerlin.common.BusinessRuleException;
import com.arcad.atumerlin.common.PageResponse;
import com.arcad.atumerlin.common.PageableFactory;
import com.arcad.atumerlin.common.ResourceNotFoundException;
import com.arcad.atumerlin.domain.Article;
import com.arcad.atumerlin.dto.ArticleDtos.ArticleRequest;
import com.arcad.atumerlin.dto.ArticleDtos.ArticleResponse;
import com.arcad.atumerlin.repository.ArticleRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class ArticleService {

    private final ArticleRepository repository;
    private final PageableFactory pageableFactory;

    public ArticleService(ArticleRepository repository, PageableFactory pageableFactory) {
        this.repository = repository;
        this.pageableFactory = pageableFactory;
    }

    @Transactional(readOnly = true)
    public PageResponse<ArticleResponse> list(String search, String familyId, Integer page, Integer size) {
        var pageable = pageableFactory.of(page, size, Sort.by("id").ascending());
        var result = StringUtils.hasText(familyId)
                ? repository.findByDeletedFalseAndFamilyId(familyId, pageable)
                : StringUtils.hasText(search)
                        ? repository.findByDeletedFalseAndDescriptionContainingIgnoreCase(search, pageable)
                        : repository.findByDeletedFalse(pageable);
        return PageResponse.from(result, ArticleService::toResponse);
    }

    @Transactional(readOnly = true)
    public ArticleResponse get(String id) {
        return toResponse(find(id));
    }

    /** Used by the order service; returns the (non-deleted) article entity. */
    @Transactional(readOnly = true)
    public Article require(String id) {
        return find(id);
    }

    public ArticleResponse create(ArticleRequest request) {
        String id = request.id().toUpperCase();
        if (repository.existsById(id)) {
            throw new BusinessRuleException("Article already exists: " + id);
        }
        Article article = new Article();
        article.setId(id);
        apply(article, request);
        return toResponse(repository.save(article));
    }

    public ArticleResponse update(String id, ArticleRequest request) {
        Article article = find(id);
        apply(article, request);
        return toResponse(repository.save(article));
    }

    public void delete(String id) {
        Article article = find(id);
        article.setDeleted(true);
        repository.save(article);
    }

    private Article find(String id) {
        Article article = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", id));
        if (article.isDeleted()) {
            throw new ResourceNotFoundException("Article", id);
        }
        return article;
    }

    private void apply(Article article, ArticleRequest request) {
        article.setDescription(request.description());
        article.setSalePrice(Optional.ofNullable(request.salePrice()).orElse(BigDecimal.ZERO));
        article.setWarehousePrice(Optional.ofNullable(request.warehousePrice()).orElse(BigDecimal.ZERO));
        article.setFamilyId(request.familyId());
        article.setStock(Optional.ofNullable(request.stock()).orElse(0));
        article.setMinStock(Optional.ofNullable(request.minStock()).orElse(0));
        article.setCustomerOrderQty(Optional.ofNullable(request.customerOrderQty()).orElse(0));
        article.setPurchaseOrderQty(Optional.ofNullable(request.purchaseOrderQty()).orElse(0));
        article.setVatCode(request.vatCode());
        article.setInformation(request.information());
    }

    static ArticleResponse toResponse(Article a) {
        return new ArticleResponse(
                a.getId(), a.getDescription(), a.getSalePrice(), a.getWarehousePrice(),
                a.getFamilyId(), a.getStock(), a.getMinStock(), a.getCustomerOrderQty(),
                a.getPurchaseOrderQty(), a.getVatCode(), a.getInformation());
    }
}
