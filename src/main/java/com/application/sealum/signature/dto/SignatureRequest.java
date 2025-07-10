package com.application.sealum.signature.dto;

import lombok.Data;

@Data
public class SignatureRequest {
    private String documentId;
    private String verifierId;
    private String algorithm;
}
