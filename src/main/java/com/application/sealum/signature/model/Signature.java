package com.application.sealum.signature.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "signatures")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signature {

    @Id
    private String id;

    private String documentId;
    private String verifierId;

    private String signature;
    private String algorithm;

    private String publicKeySnapshot;

    private Instant signedAt;

}
