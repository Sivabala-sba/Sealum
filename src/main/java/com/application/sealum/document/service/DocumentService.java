package com.application.sealum.document.service;

import com.application.sealum.common.config.FileStorageConfig;
import com.application.sealum.document.model.StoredDocument;
import com.application.sealum.document.model.enums.DocumentStatus;
import com.application.sealum.document.repository.DocumentRepository;
import com.application.sealum.document.util.HashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final FileStorageConfig fileStorageConfig;

    public StoredDocument uploadDocument(MultipartFile file, String ownerId) throws Exception {
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String filePath = fileStorageConfig.getUploadDir() + File.separator + fileName;

        //Save file to disk
        File targetFile = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(targetFile)){
            fos.write(file.getBytes());
        }

        //Generate hash
        String hash;
        try (FileInputStream fis = new FileInputStream(targetFile)){
            hash = HashUtil.sha256(fis);
        }

        //Check for duplicates
        if(documentRepository.existsByHash(hash)){
            throw new IllegalArgumentException("Document already exists.");
        }

        //Save metadata
        StoredDocument document = StoredDocument.builder()
                .ownerId(ownerId)
                .fileName(file.getOriginalFilename())
                .fileType(file.getContentType())
                .filePath(filePath)
                .hash(hash)
                .status(DocumentStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        return documentRepository.save(document);
    }
}
