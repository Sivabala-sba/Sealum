package com.application.sealum.signature.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SignatureResponse {
    private String signatureId;
    private String documentId;
    private String verifierId;
    private String algorithm;
    private Instant signedAt;
}
