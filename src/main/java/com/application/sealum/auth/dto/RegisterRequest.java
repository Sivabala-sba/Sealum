package com.application.sealum.auth.dto;

import com.application.sealum.user.model.enums.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private UserRole role;
}
