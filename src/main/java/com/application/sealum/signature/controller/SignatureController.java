package com.application.sealum.signature.controller;

import com.application.sealum.signature.dto.SignatureRequest;
import com.application.sealum.signature.dto.SignatureResponse;
import com.application.sealum.signature.model.Signature;
import com.application.sealum.signature.service.SignatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/signatures")
@RequiredArgsConstructor
public class SignatureController {

    private final SignatureService signatureService;

    @PostMapping("/sign")
    public ResponseEntity<?> signDocument(@RequestBody SignatureRequest request){
        try{
            Signature signature = signatureService.signDocument(request);
            SignatureResponse response = SignatureResponse.builder()
                    .signatureId(signature.getId())
                    .documentId(signature.getDocumentId())
                    .verifierId(signature.getVerifierId())
                    .algorithm(signature.getAlgorithm())
                    .signedAt(signature.getSignedAt())
                    .build();

            return ResponseEntity.ok(response);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
