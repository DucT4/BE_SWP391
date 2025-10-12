package com.SWP391_02.service;

import com.SWP391_02.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class TokenService {

    // 64-byte base64 string (nên để trong application.properties hoặc env)
    private static final String SECRET_B64 = "NGJiNmQxZGZiYWZiNjRhNjgxMTM5ZDE1ODZiNmYxMTYwZDE4MTU5YWZkNTdjOGM3OTEzNmQ3NDkwNjMwNDA3Yw==";
    private static final long EXP_MS = 24 * 60 * 60 * 1000L; // 24h

    private SecretKey key() {
        byte[] bytes = Decoders.BASE64.decode(SECRET_B64);
        return Keys.hmacShaKeyFor(bytes);
    }

    // ====== Generate ======
    public String generateToken(User user) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + EXP_MS);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId", user.getId()) // Add userId to token
                .claim("role", "ROLE_" + user.getRole().name())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key())
                .compact();
    }

    // ====== Extract ======
    public String extractUsername(String token) {
        return extractAll(token).getSubject();
    }

    public Long extractUserId(String token) {
        return extractAll(token).get("userId", Long.class);
    }

    public String extractRole(String token) {
        return extractAll(token).get("role", String.class);
    }

    public boolean isExpired(String token) {
        return extractAll(token).getExpiration().before(new Date());
    }

    private Claims extractAll(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // ====== New methods for HTTP request ======
    public String extractTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public Long getUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            return extractUserId(token);
        }
        return null;
    }

    public String getRoleFromRequest(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            return extractRole(token);
        }
        return null;
    }

    // ====== Validate ======
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
}
