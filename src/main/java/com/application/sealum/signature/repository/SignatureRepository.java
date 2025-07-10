package com.application.sealum.signature.repository;

import com.application.sealum.signature.model.Signature;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SignatureRepository extends MongoRepository<Signature, String> {
    boolean existsByDocumentId(String documentId);
}
