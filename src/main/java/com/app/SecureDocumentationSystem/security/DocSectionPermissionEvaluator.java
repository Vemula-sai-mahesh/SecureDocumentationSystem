package com.app.SecureDocumentationSystem.security;

import com.app.SecureDocumentationSystem.model.Role;
import com.app.SecureDocumentationSystem.model.RoleDocSection;
import com.app.SecureDocumentationSystem.model.User;
import com.app.SecureDocumentationSystem.model.UserRole;
import com.app.SecureDocumentationSystem.repository.RoleDocSectionRepository;
import com.app.SecureDocumentationSystem.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DocSectionPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleDocSectionRepository roleDocSectionRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || !(authentication.getPrincipal() instanceof UserDetails) || !(permission instanceof String)) {
            return false;
        }

        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();
        if ("DOCSECTION".equals(targetType) && "view".equals(permission.toString().toLowerCase())) {
             // Assuming targetDomainObject is the path_slug of the document section
            String docSectionPathSlug = (String) targetDomainObject;
            return hasViewPermissionForDocSection(authentication, docSectionPathSlug);
        }

        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if ((authentication == null) || !(authentication.getPrincipal() instanceof UserDetails) || !(permission instanceof String)) {
            return false;
        }

        if ("DOCSECTION".equals(targetType.toUpperCase()) && "view".equals(permission.toLowerCase())) {
            // Assuming targetId is the ID of the document section
            Long docSectionId = (Long) targetId;
             // You would need to fetch the path_slug from the ID here
             // For simplicity, this method might not be used if you are always checking against path_slug
             return false; // Or implement logic to find path_slug by ID and call hasViewPermissionForDocSection
        }

        return false;
    }

    private boolean hasViewPermissionForDocSection(Authentication authentication, String docSectionPathSlug) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Find the user (assuming username is unique)
        // This would ideally be done in a UserService, but for the evaluator, we can fetch directly
        User user = null; // Fetch user by username from UserRepository

        if (user == null) {
            return false;
        }

        // Get all roles for the user
        Set<Long> userRoleIds = userRoleRepository.findByUser(user).stream()
                .map(userRole -> userRole.getRole().getId())
                .collect(Collectors.toSet());

        if (userRoleIds.isEmpty()) {
            return false;
        }

        // Check if any of the user's roles are mapped to the document section
        return roleDocSectionRepository.existsByRole_IdInAndDocSection_PathSlug(userRoleIds, docSectionPathSlug);
    }
}