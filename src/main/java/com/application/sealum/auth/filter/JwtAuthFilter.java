package com.application.sealum.auth.filter;

import com.application.sealum.common.config.JwtConfig;
import com.application.sealum.common.util.KeyLoader;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.PublicKey;
import java.security.Signature;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements Filter {

    private final JwtConfig jwtConfig;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException{
        HttpServletRequest request = (HttpServletRequest) req;
        String header = request.getHeader("Authorization");

        if(header == null || !header.startsWith("Bearer ")){
            chain.doFilter(req, res);
            return;
        }

        try{
            String token = header.substring(7);
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Malformed JWT");

            String unsigned = parts[0] + "." + parts[1];
            byte[] signatureBytes = Base64.getUrlDecoder().decode(parts[2]);

            PublicKey publicKey = KeyLoader.loadPublicKey(jwtConfig.getPublicKeyPath());
            Signature verifier = Signature.getInstance("SHA256withRSA");
            verifier.initVerify(publicKey);
            verifier.update(unsigned.getBytes());
            if(!verifier.verify(signatureBytes)) throw new SecurityException("Invalid signature");

            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            Map<String, Object> payload = new ObjectMapper().readValue(payloadJson, Map.class);

            long exp = ((Number) payload.get("exp")).longValue();
            if(Instant.now().getEpochSecond() > exp) throw new SecurityException("Token expired");

            String userId = (String) payload.get("sub");
            String role = (String) payload.get("role");

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                    userId,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );

            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(req, res);

        } catch (Exception e) {
            ((HttpServletResponse) res).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.getWriter().write("Unauthorized: " + e.getMessage());
        }
    }
}
