package com.app.SecureDocumentationSystem.controller;

import com.app.SecureDocumentationSystem.model.DocSection;
import com.app.SecureDocumentationSystem.repository.DocSectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/docsections")
public class DocSectionController {

    @Autowired
    private DocSectionRepository docSectionRepository;

    @GetMapping
    public List<DocSection> getAllDocSections() {
        return docSectionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocSection> getDocSectionById(@PathVariable Long id) {
        Optional<DocSection> docSection = docSectionRepository.findById(id);
        return docSection.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<DocSection> createDocSection(@RequestBody DocSection docSection) {
        DocSection savedDocSection = docSectionRepository.save(docSection);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDocSection);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocSection> updateDocSection(@PathVariable Long id, @RequestBody DocSection docSectionDetails) {
        Optional<DocSection> optionalDocSection = docSectionRepository.findById(id);
        if (optionalDocSection.isPresent()) {
            DocSection docSection = optionalDocSection.get();
            docSection.setName(docSectionDetails.getName());
            docSection.getPathSlug(docSectionDetails.getPathSlug()); // Assuming you have a setPathSlug method
            DocSection updatedDocSection = docSectionRepository.save(docSection);
            return ResponseEntity.ok(updatedDocSection);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocSection(@PathVariable Long id) {
        if (docSectionRepository.existsById(id)) {
            docSectionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}