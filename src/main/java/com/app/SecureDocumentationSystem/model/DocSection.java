package com.app.SecureDocumentationSystem.model;

import javax.persistence.*;

@Entity
@Table(name = "doc_sections")
public class DocSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "path_slug")
    private String pathSlug;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathSlug() {
        return pathSlug;
    }

    public void setPathSlug(String pathSlug) {
        this.pathSlug = pathSlug;
    }
}