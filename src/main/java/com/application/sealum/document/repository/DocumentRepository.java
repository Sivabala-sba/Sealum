package com.application.sealum.document.repository;

import com.application.sealum.document.model.StoredDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DocumentRepository extends MongoRepository<StoredDocument, String> {
    boolean existsByHash(String hash);
}
