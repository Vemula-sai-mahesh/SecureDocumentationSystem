package com.app.SecureDocumentationSystem.service;

import com.app.SecureDocumentationSystem.model.DocSection;
import com.app.SecureDocumentationSystem.repository.DocSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocSectionService {

    private final DocSectionRepository docSectionRepository;

    @Autowired
    public DocSectionService(DocSectionRepository docSectionRepository) {
        this.docSectionRepository = docSectionRepository;
    }

    public DocSection createDocSection(DocSection docSection) {
        return docSectionRepository.save(docSection);
    }

    public List<DocSection> getAllDocSections() {
        return docSectionRepository.findAll();
    }

    public Optional<DocSection> findDocSectionByPathSlug(String pathSlug) {
        return docSectionRepository.findByPathSlug(pathSlug);
    }

    public void deleteDocSection(Long id) {
        docSectionRepository.deleteById(id);
    }
}