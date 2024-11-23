package tn.enicar.enicar_platforme.document;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.enicar_platforme.user.User;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    // Endpoint to upload a document

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file,
                                                 @AuthenticationPrincipal User user) {
        try {
            documentService.uploadFile(file, user);
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error uploading file");
        }
    }

    // Endpoint to get all documents uploaded by a user (student/professor)
    @GetMapping("/user")
    public ResponseEntity<List<DocumentResponse>> getDocumentsByUser(@AuthenticationPrincipal User user) {
        List<DocumentResponse> documents = documentService.getDocumentsByUser(user);
        return ResponseEntity.ok(documents);
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/")
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        var documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }
    // Endpoint to get a specific document by ID
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponse> getDocumentById(@PathVariable Long id) {
        var document = documentService.getDocumentById(id);
        return ResponseEntity.ok(document);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            documentService.deleteDocument(id, user);  // Pass the current user to the service method
            return ResponseEntity.ok("Document deleted successfully");
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(403).body("Error deleting file: " + e.getMessage());  // 403 for Unauthorized
        }
    }



}
