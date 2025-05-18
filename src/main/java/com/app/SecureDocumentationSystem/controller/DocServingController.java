package com.app.SecureDocumentationSystem.controller;

import com.app.SecureDocumentationSystem.model.DocSection;
import com.app.SecureDocumentationSystem.service.DocSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/docs")
public class DocServingController {

    @Autowired
    private DocSectionService docSectionService;

    private final Path siteRoot = Paths.get("site");

    @GetMapping("/{*path}")
    public ResponseEntity<Resource> serveDocumentation(HttpServletRequest request) {
        // Get the full path requested after /docs/
        String requestedPath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        requestedPath = requestedPath.substring("/docs/".length());

        // Determine the path slug for permission checking
        String pathSlug = extractPathSlug(requestedPath);

        Path filePath = siteRoot.resolve(requestedPath).normalize();

        // If the requested path is a directory, append index.html
        if (filePath.toFile().isDirectory() || !StringUtils.hasText(requestedPath)) {
            filePath = filePath.resolve("index.html").normalize();
        }


        try {
            Optional<DocSection> docSectionOptional = docSectionService.findDocSectionByPathSlug(pathSlug);

            if (!docSectionOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            Path file = siteRoot.resolve(requestedPath).normalize();

            if (!filePath.startsWith(siteRoot)) {
                // Prevent directory traversal
                return ResponseEntity.badRequest().build();
            }            

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                HttpHeaders headers = new HttpHeaders();
                // Determine content type based on file extension (simplified)
                String fileExtension = StringUtils.getFilenameExtension(filePath.toString());
                if ("html".equalsIgnoreCase(fileExtension)) {
                    headers.setContentType(MediaType.TEXT_HTML);
                } else if ("css".equalsIgnoreCase(fileExtension)) {
                    headers.setContentType(MediaType.TEXT_CSS);
                } else if ("js".equalsIgnoreCase(fileExtension)) {
                    headers.setContentType(MediaType.valueOf("application/javascript"));
                } else if ("png".equalsIgnoreCase(fileExtension)) {
                    headers.setContentType(MediaType.IMAGE_PNG);
                } else if ("jpg".equalsIgnoreCase(fileExtension) || "jpeg".equalsIgnoreCase(fileExtension)) {
                    headers.setContentType(MediaType.IMAGE_JPEG);
                } else if ("gif".equalsIgnoreCase(fileExtension)) {
                    headers.setContentType(MediaType.IMAGE_GIF);
                } else if ("svg".equalsIgnoreCase(fileExtension)) {
 headers.setContentType(MediaType.valueOf("image/svg+xml"));
                } else if (path.endsWith(".json")) {
 headers.setContentType(MediaType.APPLICATION_JSON);
                } else if (path.endsWith(".pdf")) {
 headers.setContentType(MediaType.APPLICATION_PDF);
                } else {
                    headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                }
                return ResponseEntity.ok().headers(headers).body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (AccessDeniedException e) {
            // Handled by Spring Security's AccessDeniedHandler if configured, or default
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Log the exception in a real application
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String extractPathSlug(String fullPath) {
        // Assuming path format is {path-slug}/... or just {path-slug}
        // This needs to be refined based on your MkDocs structure and how you map sections
        // A simple approach might be to take the first part of the path after /docs/
        String cleanedPath = fullPath;
        int firstSlash = cleanedPath.indexOf('/');
        if (firstSlash != -1) {
            return cleanedPath.substring(0, firstSlash);
        }
        return cleanedPath; // This could be the slug for the root section
    }
}