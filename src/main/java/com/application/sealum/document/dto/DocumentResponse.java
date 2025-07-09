package com.application.sealum.document.dto;

import com.application.sealum.document.model.enums.DocumentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DocumentResponse {
    private String id;
    private String fileName;
    private String fileType;
    private String hash;
    private DocumentStatus status;
    private Instant createdAt;
    private Instant signedAt;
}
