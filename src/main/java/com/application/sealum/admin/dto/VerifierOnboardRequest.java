package com.application.sealum.admin.dto;

import lombok.Data;

@Data
public class VerifierOnboardRequest {
    private String username;
    private String email;
    private String password;
    private String publickey; //PEM format
}
