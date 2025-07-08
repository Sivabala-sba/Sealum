package com.application.sealum.document.model;

import com.application.sealum.document.model.enums.DocumentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.Instant;

@Document(collection = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoredDocument {

    @Id
    private String id;

    private String ownerId;
    private String fileName;
    private String fileType;
    private String filePath;
    private String hash;

    private DocumentStatus status;

    private Instant createdAt;
    private Instant signedAt;

    private String signatureId;

}
