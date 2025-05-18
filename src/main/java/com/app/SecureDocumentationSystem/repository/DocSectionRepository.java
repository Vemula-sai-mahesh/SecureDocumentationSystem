package com.app.SecureDocumentationSystem.repository;

import com.app.SecureDocumentationSystem.model.DocSection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocSectionRepository extends JpaRepository<DocSection, Long> {
    Page<DocSection> findAll(Pageable pageable);
}