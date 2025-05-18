package com.app.SecureDocumentationSystem.repository;

import com.app.SecureDocumentationSystem.model.UserRole;
import com.app.SecureDocumentationSystem.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}