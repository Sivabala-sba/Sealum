package com.application.sealum.signature.service;

import com.application.sealum.common.config.JwtConfig;
import com.application.sealum.common.util.KeyLoader;
import com.application.sealum.document.model.StoredDocument;
import com.application.sealum.document.model.enums.DocumentStatus;
import com.application.sealum.document.repository.DocumentRepository;
import com.application.sealum.signature.dto.SignatureRequest;
import com.application.sealum.signature.model.Signature;
import com.application.sealum.signature.repository.SignatureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SignatureService {

    private final SignatureRepository signatureRepository;
    private final DocumentRepository documentRepository;
    private final JwtConfig jwtConfig;

    public Signature signDocument(SignatureRequest request) throws Exception{
        StoredDocument doc = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        if(signatureRepository.existsByDocumentId(doc.getId())){
            throw new IllegalArgumentException("Document already signed");
        }

        //Load private key
        PrivateKey privateKey = KeyLoader.loadPrivateKey(jwtConfig.getPrivateKeyPath());

        //Sign the hash
        java.security.Signature signer = java.security.Signature.getInstance(request.getAlgorithm());
        signer.initSign(privateKey);
        signer.update(doc.getHash().getBytes(StandardCharsets.UTF_8));
        byte[] signatureBytes = signer.sign();
        String signatureBase64 = Base64.getEncoder().encodeToString(signatureBytes);

        //Store signature
        Signature signature = Signature.builder()
                .documentId(doc.getId())
                .verifierId(request.getVerifierId())
                .signature(signatureBase64)
                .algorithm(request.getAlgorithm())
                .publicKeySnapshot(KeyLoader.loadPublicKey(jwtConfig.getPublicKeyPath()).toString())
                .signedAt(Instant.now())
                .build();

        Signature saved = signatureRepository.save(signature);

        //Update document status
        doc.setStatus(DocumentStatus.SIGNED);
        doc.setSignedAt(saved.getSignedAt());
        doc.setSignatureId(saved.getId());
        documentRepository.save(doc);

        return saved;
    }
}
