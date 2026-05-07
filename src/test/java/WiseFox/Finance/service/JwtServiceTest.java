package WiseFox.Finance.service;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private static final String SECRET = "this-is-a-very-long-secret-key-for-testing-purposes-1234567890";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L);
    }

    // ── GENERATE & VALIDATE TOKEN ─────────────────────────────────────────────

    @Test
    void generateToken_thenValidate_success() {
        String token = jwtService.generateToken(1L, "john@example.com");

        assertThat(token).isNotBlank();

        Claims claims = jwtService.validateToken(token);
        assertThat(claims.getSubject()).isEqualTo("1");
        assertThat(claims.get("email", String.class)).isEqualTo("john@example.com");
    }

    @Test
    void getUserIdFromToken_returnsCorrectId() {
        String token = jwtService.generateToken(42L, "test@example.com");

        Long userId = jwtService.getUserIdFromToken(token);

        assertThat(userId).isEqualTo(42L);
    }

    @Test
    void validateToken_invalidToken_throws() {
        assertThatThrownBy(() -> jwtService.validateToken("this.is.invalid"))
                .isInstanceOf(Exception.class);
    }

    // ── GOOGLE REGISTRATION TOKEN ─────────────────────────────────────────────

    @Test
    void generateGoogleRegistrationToken_thenGetEmail_success() {
        String token = jwtService.generateGoogleRegistrationToken("google@example.com");

        assertThat(token).isNotBlank();

        String email = jwtService.getEmailFromGoogleRegistrationToken(token);
        assertThat(email).isEqualTo("google@example.com");
    }

    @Test
    void getEmailFromGoogleRegistrationToken_withRegularToken_throws() {
        // A regular user token must NOT be accepted as a google registration token
        String regularToken = jwtService.generateToken(1L, "john@example.com");

        assertThatThrownBy(() -> jwtService.getEmailFromGoogleRegistrationToken(regularToken))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid registration token");
    }

    @Test
    void getEmailFromGoogleRegistrationToken_withInvalidToken_throws() {
        assertThatThrownBy(() -> jwtService.getEmailFromGoogleRegistrationToken("bad.token.here"))
                .isInstanceOf(ResponseStatusException.class);
    }
}
