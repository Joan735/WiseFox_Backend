package WiseFox.Finance.service;

import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.AuthRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("password123");
        user.setRole(User.Role.USER);
    }

    // ── REGISTER ─────────────────────────────────────────────────────────────

    @Test
    void register_success() {
        when(authRepository.existsByUsername("johndoe")).thenReturn(false);
        when(authRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$hashed");
        when(authRepository.save(any(User.class))).thenReturn(user);

        User result = authService.register(user);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("johndoe");
        verify(passwordEncoder).encode("password123");
        verify(authRepository).save(user);
    }

    @Test
    void register_duplicateUsername_throwsConflict() {
        when(authRepository.existsByUsername("johndoe")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Username");
    }

    @Test
    void register_duplicateEmail_throwsConflict() {
        when(authRepository.existsByUsername("johndoe")).thenReturn(false);
        when(authRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Email");
    }

    // ── LOGIN ─────────────────────────────────────────────────────────────────

    @Test
    void login_success() {
        user.setPassword("$2a$hashed");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "$2a$hashed")).thenReturn(true);

        User result = authService.login("john@example.com", "password123");

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void login_emailNotFound_throwsUnauthorized() {
        when(authRepository.findByEmail("wrong@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("wrong@example.com", "password123"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid email or password");
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        user.setPassword("$2a$hashed");
        when(authRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "$2a$hashed")).thenReturn(false);

        assertThatThrownBy(() -> authService.login("john@example.com", "wrongpass"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
