package com.application.sealum.user.model;

import com.application.sealum.user.model.enums.UserRole;
import com.application.sealum.user.model.enums.UserStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String username;
    private String email;
    private String passwordHash;

    private UserRole role;

    private String publicKey; //Only for VERIFIERs

    private Instant createdAt;
    private UserStatus status;

}
