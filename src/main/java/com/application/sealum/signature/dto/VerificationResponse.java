package com.application.sealum.signature.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class VerificationResponse {
    private boolean isValid;
    private String signedBy;
    private Instant signedAt;
    private String algorithm;
    private String statusMessage;
}
