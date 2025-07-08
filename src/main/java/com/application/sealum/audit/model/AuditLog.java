package com.application.sealum.audit.model;

import com.application.sealum.audit.model.enums.AuditAction;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    private String id;

    private AuditAction action;
    private String actorId;
    private String documentId;

    private Instant timestamp;

    private Map<String, Object> details;

}
