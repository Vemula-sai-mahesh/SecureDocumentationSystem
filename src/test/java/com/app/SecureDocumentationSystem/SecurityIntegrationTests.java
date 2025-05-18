package com.app.SecureDocumentationSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.app.SecureDocumentationSystem.model.DocSection;
import com.app.SecureDocumentationSystem.model.Role;
import com.app.SecureDocumentationSystem.model.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.app.SecureDocumentationSystem.model.RoleDocSection;
import com.app.SecureDocumentationSystem.model.UserRole;
import com.app.SecureDocumentationSystem.repository.DocSectionRepository;
import com.app.SecureDocumentationSystem.repository.RoleDocSectionRepository;
import com.app.SecureDocumentationSystem.repository.RoleRepository;
import com.app.SecureDocumentationSystem.repository.UserRepository;
import com.app.SecureDocumentationSystem.repository.UserRoleRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test") // Assuming you'll use a test profile for in-memory database
@Transactional // Rollback changes after each test
public class SecurityIntegrationTests {
    
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    // Add test methods here

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DocSectionRepository docSectionRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private RoleDocSectionRepository roleDocSectionRepository;

    private User testUser;
    private Role testRole;
    private DocSection testDocSection;

    @BeforeEach
    public void setup() {
        // Clear repositories (useful if not using @Transactional or needing clean state)
        roleDocSectionRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        docSectionRepository.deleteAll();

        // Create test data
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPasswordHash("$2a$10$123456789012345678901.hashedpassword"); // Use a real hashed password in a real app
        testUser.setEmail("testuser@example.com");
        testUser.setEnabled(true);
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        mockMvc.perform(post("/auth/login")
 .contentType("application/json")
 .content("{\"username\": \"testuser\", \"password\": \"password\"}")) // Replace with valid test user credentials
               .andExpect(status().isOk());
    }

    @Test
    public void testLoginFailure() throws Exception {
        mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"username\": \"nonexistentuser\", \"password\": \"wrongpassword\"}"))
                .andExpect(status().isUnauthorized());
    }
 @Test
    public void testProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/admin/users"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    public void testProtectedEndpointWithValidToken() throws Exception {
        // Need to create a test user with known credentials and roles in setup
        MvcResult result = mockMvc.perform(post("/auth/login").contentType("application/json")
               .content("{\"username\": \"testuser\", \"password\": \"password\"}")) // Replace with valid test user credentials
               .andExpect(status().isOk())
               .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String token = jsonNode.get("token").asText(); // Assuming the token is returned in a field named "token"

        mockMvc.perform(get("/admin/users")
               .header("Authorization", "Bearer " + token))
               .andExpect(status().isOk());
    }

    @Test
    public void testAccessDocSectionWithPermission() throws Exception {
        // Create test data for RBAC
        testRole = new Role();
        testRole.setName("VIEW_DOC_SECTION_A");
        testRole.setDescription("Can view documentation section A");
        testRole = roleRepository.save(testRole);

        testDocSection = new DocSection();
        testDocSection.setName("Section A");
        testDocSection.setPathSlug("section-a");
        testDocSection = docSectionRepository.save(testDocSection);

        UserRole userRole = new UserRole();
        userRole.setUser(testUser);
        userRole.setRole(testRole);
        userRoleRepository.save(userRole);

        RoleDocSection roleDocSection = new RoleDocSection();
        roleDocSection.setRole(testRole);
        roleDocSection.setDocSection(testDocSection);
        roleDocSectionRepository.save(roleDocSection);

        // Authenticate the user to get a token
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        // Attempt to access the protected doc section
        mockMvc.perform(get("/docs/" + testDocSection.getPathSlug() + "/index.html") // Assuming index.html is the default file
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    public void testAccessDocSectionWithoutPermission() throws Exception {
        // Create test data for a doc section and a role, but DO NOT create a RoleDocSection mapping
        Role anotherRole = new Role();
        anotherRole.setName("ANOTHER_ROLE");
        anotherRole.setDescription("Just another role");
        anotherRole = roleRepository.save(anotherRole);

        DocSection restrictedDocSection = new DocSection();
        restrictedDocSection.setName("Restricted Section");
        restrictedDocSection.setPathSlug("restricted-section");
        restrictedDocSection = docSectionRepository.save(restrictedDocSection);

        // Assign the user a role that does NOT have access to the restricted doc section
        UserRole userRole = new UserRole();
        userRole.setUser(testUser);
        userRole.setRole(anotherRole); // Assign a role that doesn't have access
        userRoleRepository.save(userRole);

        // Authenticate the user to get a token
        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType("application/json")
                .content("{\"username\": \"testuser\", \"password\": \"password\"}"))
                .andExpect(status().isOk())
                .andReturn();
        String token = objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();

        // Attempt to access the restricted doc section
        mockMvc.perform(get("/docs/" + restrictedDocSection.getPathSlug() + "/index.html")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}