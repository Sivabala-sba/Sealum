package com.application.sealum.admin.service;

import com.application.sealum.admin.dto.VerifierOnboardRequest;
import com.application.sealum.user.model.User;
import com.application.sealum.user.model.enums.UserRole;
import com.application.sealum.user.model.enums.UserStatus;
import com.application.sealum.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public void onboardVerifier(VerifierOnboardRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email already registered.");
        }

        User verifier = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.VERIFIER)
                .publicKey(request.getPublickey())
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        userRepository.save(verifier);
    }

    public void revokeVerifier(String verifierId){
        User verifier = userRepository.findById(verifierId)
                .orElseThrow(() -> new IllegalArgumentException("Verifier not found"));

        if(verifier.getRole() != UserRole.VERIFIER){
            throw new IllegalArgumentException("User is not a verifier");
        }

        verifier.setStatus(UserStatus.REVOKED);
        userRepository.save(verifier);
    }

    public void rotateVerifierKey(String verifierId, String newPublicKey){
        User verifier = userRepository.findById(verifierId)
                .orElseThrow(() -> new IllegalArgumentException("Verifier not found"));

        verifier.setPublicKey(newPublicKey);
        userRepository.save(verifier);
    }
}
