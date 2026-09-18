package com.arcad.atumerlin.repository;

import com.arcad.atumerlin.domain.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, String> {

    Page<Article> findByDeletedFalse(Pageable pageable);

    Page<Article> findByDeletedFalseAndDescriptionContainingIgnoreCase(String description, Pageable pageable);

    Page<Article> findByDeletedFalseAndFamilyId(String familyId, Pageable pageable);
}
