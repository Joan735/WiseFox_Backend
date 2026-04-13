package WiseFox.Finance.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private static final long GOOGLE_REGISTRATION_EXPIRATION = 10 * 60 * 1000L; // 10 minutes
    private static final String GOOGLE_REGISTRATION_CLAIM = "google_registration";

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Full JWT for authenticated users (stored by frontend for all requests)
    public String generateToken(Long userId, String email) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("email", email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    // Short-lived token issued after email verification, used only for the register step
    public String generateGoogleRegistrationToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim(GOOGLE_REGISTRATION_CLAIM, true)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + GOOGLE_REGISTRATION_EXPIRATION))
                .signWith(getKey())
                .compact();
    }

    // Validates and extracts email from a googleRegistrationToken
    public String getEmailFromGoogleRegistrationToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Boolean isRegistrationToken = claims.get(GOOGLE_REGISTRATION_CLAIM, Boolean.class);
            if (!Boolean.TRUE.equals(isRegistrationToken)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Invalid registration token.");
            }
            return claims.getSubject(); // email
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Registration token is invalid or expired.");
        }
    }

    public Claims validateToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(validateToken(token).getSubject());
    }
}