package com.application.sealum.auth.service;

import com.application.sealum.auth.dto.AuthResponse;
import com.application.sealum.auth.dto.LoginRequest;
import com.application.sealum.auth.dto.RegisterRequest;
import com.application.sealum.common.config.JwtConfig;
import com.application.sealum.common.util.KeyLoader;
import com.application.sealum.user.model.User;
import com.application.sealum.user.model.enums.UserStatus;
import com.application.sealum.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtConfig jwtConfig;
    private final BCryptPasswordEncoder passwordEncoder;

    public void register(RegisterRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email already registered.");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .createdAt(Instant.now())
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) throws Exception{
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new IllegalArgumentException("Invalid credentials.");
        }

        //Build JWT payload
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtConfig.getExpiration());

        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", user.getId());
        payload.put("role", user.getRole().name());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", exp.getEpochSecond());

        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"RS256\",\"typ\":\"JWT\"}".getBytes());

        String payloadStr = new ObjectMapper().writeValueAsString(payload);
        String body = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(payloadStr.getBytes());

        String unsignedToken = header + "." + body;

        //Sign token
        PrivateKey privateKey = KeyLoader.loadPrivateKey(jwtConfig.getPrivateKeyPath());
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(privateKey);
        signer.update(unsignedToken.getBytes());
        String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(signer.sign());

        String token = unsignedToken + "." + signature;

        return AuthResponse.builder()
                .accessToken(token)
                .expiresIn(jwtConfig.getExpiration())
                .build();
    }
}
