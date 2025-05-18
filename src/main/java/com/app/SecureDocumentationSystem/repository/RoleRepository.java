package com.app.SecureDocumentationSystem.repository;

import com.app.SecureDocumentationSystem.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
    Page<Role> findAll(Pageable pageable);
    Page<Role> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String nameSearchTerm, String descriptionSearchTerm, Pageable pageable);
}