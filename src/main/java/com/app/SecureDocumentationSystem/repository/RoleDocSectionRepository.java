package com.app.SecureDocumentationSystem.repository;

import com.app.SecureDocumentationSystem.model.RoleDocSection;
import com.app.SecureDocumentationSystem.model.RoleDocSectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleDocSectionRepository extends JpaRepository<RoleDocSection, RoleDocSectionId> {
    Page<RoleDocSection> findAll(Pageable pageable);
}