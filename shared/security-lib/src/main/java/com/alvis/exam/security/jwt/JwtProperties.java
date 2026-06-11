package com.alvis.exam.security.jwt;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("jwt")
public class JwtProperties {
    private String secret = "exam-jwt-secret-key-must-be-at-least-32-chars";
    private long expirationMs = 86400000L;
}
