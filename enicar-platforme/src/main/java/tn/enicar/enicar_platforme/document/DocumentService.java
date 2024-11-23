package tn.enicar.enicar_platforme.document;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.enicar.enicar_platforme.user.User;
import tn.enicar.enicar_platforme.user.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocumentService {

    @Value("${document.upload.dir}")  // Load the upload directory from application.properties
    private String uploadDir;

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;


    public Document uploadFile(MultipartFile file, User user) throws IOException {
        // Generate a unique filename
        String filename = file.getOriginalFilename()+ "_" + System.currentTimeMillis()  ;
        Path targetLocation = Path.of(uploadDir, filename);

        // Save the file to the specified directory
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Use Lombok builder pattern to create and save Document
        Document document = Document.builder()
                .filename(filename)
                .fileUrl(targetLocation.toString())  // Store file URL (local path)
                .uploadDate(LocalDateTime.now())
                .user(user)  // Associate the document with the user (student/professor)
                .build();

        return documentRepository.save(document);
    }


    public List<DocumentResponse> getDocumentsByUser(User user) {
        var documents = documentRepository.findByUser(user);

        return documents.stream()
                .map(doc -> {
                    var userFromDoc = doc.getUser();
                    return DocumentResponse.builder()
                            .id(doc.getId())
                            .filename(doc.getFilename())
                            .fileUrl(doc.getFileUrl())
                            .uploadDate(doc.getUploadDate())
                            .userId(userFromDoc.getId())
                            .userFullName(userFromDoc.fullName())
                            .userRole(userFromDoc.getRole().getName()) // Assuming the User entity has a Role association
                            .build();
                })
                .collect(Collectors.toList());
    }
    public List<DocumentResponse> getAllDocuments() {
        var documents = documentRepository.findAll();

        return documents.stream()
                .map(doc -> {
                    var userFromDoc = doc.getUser();
                    return DocumentResponse.builder()
                            .id(doc.getId())
                            .filename(doc.getFilename())
                            .fileUrl(doc.getFileUrl())
                            .uploadDate(doc.getUploadDate())
                            .userId(userFromDoc.getId())
                            .userFullName(userFromDoc.fullName())
                            .userRole(userFromDoc.getRole().getName()) // Assuming the User entity has a Role association
                            .build();
                })
                .collect(Collectors.toList());
    }


    public DocumentResponse getDocumentById(Long documentId) {
        var document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        // Map Document to DocumentResponse
        var documentResponse = DocumentResponse.builder()
                .id(document.getId())
                .filename(document.getFilename())
                .fileUrl(document.getFileUrl())
                .uploadDate(document.getUploadDate())
                .userId(document.getUser().getId()) // Assuming the User has an 'id' field
                .userFullName(document.getUser().fullName()) // Assuming 'fullName()' is a method on User
                .userRole(document.getUser().getRole().getName()) // Assuming Role has 'getName()' method
                .build();

        return documentResponse;
    }


    public void deleteDocument(Long documentId, User user) throws IOException {
        var document = getDocumentById(documentId);  // Retrieve the document as a DTO

        // Check if the user is an admin or the owner of the document
        if (!user.getRole().getName().equals("ADMIN") && !document.getUserId().equals(user.getId())) {
            throw new RuntimeException("You are not authorized to delete this document");
        }

        // Delete the file from the file system
        var filePath = Path.of(document.getFileUrl());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        } else {
            throw new IOException("File not found");
        }

        // Delete document record from the database
        documentRepository.deleteById(documentId);
    }


}