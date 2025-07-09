package com.application.sealum.document.controller;

import com.application.sealum.document.dto.DocumentResponse;
import com.application.sealum.document.model.StoredDocument;
import com.application.sealum.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(@RequestParam("file") MultipartFile file,
                                            @RequestParam("ownerId") String ownerId) {
        try {
            StoredDocument document = documentService.uploadDocument(file, ownerId);
            DocumentResponse response = DocumentResponse.builder()
                    .id(document.getId())
                    .fileName(document.getFileName())
                    .fileType(document.getFileType())
                    .hash(document.getHash())
                    .status(document.getStatus())
                    .createdAt(document.getCreatedAt())
                    .signedAt(document.getSignedAt())
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
