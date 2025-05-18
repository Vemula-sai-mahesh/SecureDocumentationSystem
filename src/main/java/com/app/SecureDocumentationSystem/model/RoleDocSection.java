package com.app.SecureDocumentationSystem.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "role_doc_section")
public class RoleDocSection implements Serializable {

    @EmbeddedId
    private RoleDocSectionId id;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @MapsId("docSectionId")
    @JoinColumn(name = "doc_section_id")
    private DocSection docSection;

    public RoleDocSection() {
    }

    public RoleDocSection(Role role, DocSection docSection) {
        this.role = role;
        this.docSection = docSection;
        this.id = new RoleDocSectionId(role.getId(), docSection.getId());
    }

    public RoleDocSectionId getId() {
        return id;
    }

    public void setId(RoleDocSectionId id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public DocSection getDocSection() {
        return docSection;
    }

    public void setDocSection(DocSection docSection) {
        this.docSection = docSection;
    }

    @Embeddable
    public static class RoleDocSectionId implements Serializable {

        @Column(name = "role_id")
        private Long roleId;

        @Column(name = "doc_section_id")
        private Long docSectionId;

        public RoleDocSectionId() {
        }

        public RoleDocSectionId(Long roleId, Long docSectionId) {
            this.roleId = roleId;
            this.docSectionId = docSectionId;
        }

        public Long getRoleId() {
            return roleId;
        }

        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }

        public Long getDocSectionId() {
            return docSectionId;
        }

        public void setDocSectionId(Long docSectionId) {
            this.docSectionId = docSectionId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            RoleDocSectionId that = (RoleDocSectionId) o;
            return Objects.equals(roleId, that.roleId) &&
                   Objects.equals(docSectionId, that.docSectionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(roleId, docSectionId);
        }
    }
}