package com.app.SecureDocumentationSystem.controller;

import com.app.SecureDocumentationSystem.model.Role;
import com.app.SecureDocumentationSystem.model.DocSection;
import com.app.SecureDocumentationSystem.model.User;
import com.app.SecureDocumentationSystem.model.RoleDocSection;
import com.app.SecureDocumentationSystem.repository.RoleDocSectionRepository;
import com.app.SecureDocumentationSystem.repository.RoleRepository;
import com.app.SecureDocumentationSystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.app.SecureDocumentationSystem.repository.DocSectionRepository;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DocSectionRepository docSectionRepository;

 @Autowired
 private RoleDocSectionRepository roleDocSectionRepository;

    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String listUsers(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String sort, @RequestParam(required = false) String searchTerm, Model model) {
        model.addAttribute("userPage", userService.findUsers(searchTerm, Pageable.ofSize(size).withPage(page).withSort(Sort.by(sort.split(",")))));
        model.addAttribute("newUser", new User());
        return "admin/users";
    }

    @PostMapping("/users")
    public String createUser(@ModelAttribute User user) {
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{userId}")
    public String viewUser(@PathVariable Long userId, Model model) {
        userService.findUserById(userId).ifPresent(user -> model.addAttribute("user", user));
        return "admin/view-user"; // Assuming a template for viewing a single user
    }

    @GetMapping("/users/edit/{userId}")
    public String editUserForm(@PathVariable Long userId, Model model) {
        userService.findUserById(userId).ifPresent(user -> model.addAttribute("user", user));
        return "admin/edit-user"; // Assuming a template for editing a user
    }

    @PostMapping("/users/edit/{userId}")
    public String updateUser(@PathVariable Long userId, @ModelAttribute User user) {
        // In a real application, you'd likely fetch the existing user,
        // update its properties from the form data, and then save it.
        // This simple example overwrites the existing user.
        user.setId(userId); // Ensure the ID is set for update
        userService.saveUser(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        return "redirect:/admin/users";
    }

    @GetMapping("/roles")
 public String listRoles(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String sort, @RequestParam(required = false) String searchTerm, Model model) {
 Page<Role> rolePage = roleRepository.findAll(Pageable.ofSize(size).withPage(page));
 model.addAttribute("rolePage", rolePage);
        model.addAttribute("newRole", new Role());
        return "admin/roles";
    }

    @PostMapping("/roles")
    public String createRole(@ModelAttribute Role role) {
        roleRepository.save(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/{roleId}")
    public String viewRole(@PathVariable Long roleId, Model model) {
        roleRepository.findById(roleId).ifPresent(role -> model.addAttribute("role", role));
        return "admin/view-role"; // Assuming a template for viewing a single role
    }

    @GetMapping("/roles/edit/{roleId}")
    public String editRoleForm(@PathVariable Long roleId, Model model) {
        roleRepository.findById(roleId).ifPresent(role -> model.addAttribute("role", role));
        return "admin/edit-role"; // Assuming a template for editing a role
    }

    @PostMapping("/roles/edit/{roleId}")
    public String updateRole(@PathVariable Long roleId, @ModelAttribute Role role) {
        role.setId(roleId); // Ensure the ID is set for update
        roleRepository.save(role);
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/delete/{roleId}")
    public String deleteRole(@PathVariable Long roleId) {
        roleRepository.deleteById(roleId);
        return "redirect:/admin/roles";
    }

    @GetMapping("/docsections")
    public String listDocSections(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String sort, Model model) {
 Page<DocSection> docSectionPage = docSectionRepository.findAll(Pageable.ofSize(size).withPage(page));
        model.addAttribute("docSectionPage", docSectionPage);
        model.addAttribute("newDocSection", new DocSection());
        return "admin/docsections";
    }

    @PostMapping("/docsections")
    public String createDocSection(@ModelAttribute DocSection docSection) {
        docSectionRepository.save(docSection);
        return "redirect:/admin/docsections";
    }

    @GetMapping("/docsections/{docSectionId}")
    public String viewDocSection(@PathVariable Long docSectionId, Model model) {
        docSectionRepository.findById(docSectionId).ifPresent(docSection -> model.addAttribute("docSection", docSection));
        return "admin/view-docsection"; // Assuming a template for viewing a single doc section
    }

    @GetMapping("/docsections/edit/{docSectionId}")
    public String editDocSectionForm(@PathVariable Long docSectionId, Model model) {
        docSectionRepository.findById(docSectionId).ifPresent(docSection -> model.addAttribute("docSection", docSection));
        return "admin/edit-docsection"; // Assuming a template for editing a doc section
    }

    @PostMapping("/docsections/edit/{docSectionId}")
    public String updateDocSection(@PathVariable Long docSectionId, @ModelAttribute DocSection docSection) {
        docSection.setId(docSectionId); // Ensure the ID is set for update
        docSectionRepository.save(docSection);
        return "redirect:/admin/docsections";
    }

    @PostMapping("/docsections/delete/{docSectionId}")
    public String deleteDocSection(@PathVariable Long docSectionId) {
        docSectionRepository.deleteById(docSectionId);
        return "redirect:/admin/docsections";
    }

 @GetMapping("/role-doc-sections")
 public String listRoleDocSections(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "id,asc") String sort, Model model) {
 Page<RoleDocSection> roleDocSectionPage = roleDocSectionRepository.findAll(Pageable.ofSize(size).withPage(page).withSort(Sort.by(sort.split(","))));
 model.addAttribute("roleDocSectionPage", roleDocSectionPage);
 model.addAttribute("roles", roleRepository.findAll());
 model.addAttribute("docSections", docSectionRepository.findAll());
 model.addAttribute("newRoleDocSection", new RoleDocSection());
 return "admin/role_doc_sections";
 }

 @PostMapping("/role-doc-sections")
 public String addRoleDocSection(@ModelAttribute RoleDocSection roleDocSection) {
 // In a real application, you might want to add validation and
 // ensure the Role and DocSection entities exist before saving.
 // This simple example assumes the form provides valid IDs.
 roleDocSectionRepository.save(roleDocSection);
 return "redirect:/admin/role-doc-sections";
 }

    @PostMapping("/role-doc-sections/delete/{mappingId}")
    public String deleteRoleDocSection(@PathVariable Long mappingId) {
        roleDocSectionRepository.deleteById(mappingId);
        return "redirect:/admin/role-doc-sections";
    }
}