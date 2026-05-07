package WiseFox.Finance.service;

import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.UserRepository;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setSurname("Doe");
        user.setUsername("johndoe");
        user.setEmail("john@example.com");
        user.setPassword("plainpassword");
        user.setRole(User.Role.USER);
    }

    // ── GET BY ID ─────────────────────────────────────────────────────────────

    @Test
    void getById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getById_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");
    }

    // ── GET BY USERNAME ───────────────────────────────────────────────────────

    @Test
    void getByUsername_found() {
        when(userRepository.findByUsernameIgnoreCase("johndoe")).thenReturn(Optional.of(user));

        User result = userService.getByUsername("johndoe");

        assertThat(result.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void getByUsername_notFound_throws() {
        when(userRepository.findByUsernameIgnoreCase("ghost")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getByUsername("ghost"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found with username");
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────

    @Test
    void update_withPlainPassword_encodesIt() {
        User updated = new User();
        updated.setName("Jane");
        updated.setSurname("Doe");
        updated.setUsername("janedoe");
        updated.setEmail("jane@example.com");
        updated.setPassword("newplainpassword");
        updated.setRole(User.Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newplainpassword")).thenReturn("$2a$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.update(1L, updated);

        assertThat(result.getPassword()).isEqualTo("$2a$encoded");
        verify(passwordEncoder).encode("newplainpassword");
    }

    @Test
    void update_withBcryptPassword_doesNotReEncode() {
        User updated = new User();
        updated.setName("Jane");
        updated.setSurname("Doe");
        updated.setUsername("janedoe");
        updated.setEmail("jane@example.com");
        updated.setPassword("$2a$10$existinghash");
        updated.setRole(User.Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = userService.update(1L, updated);

        assertThat(result.getPassword()).isEqualTo("$2a$10$existinghash");
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void update_userNotFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, user))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User not found");
    }

    // ── DELETE ────────────────────────────────────────────────────────────────

    @Test
    void delete_existing_returnsTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.delete(1L);

        assertThat(result).isTrue();
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_notExisting_returnsFalse() {
        when(userRepository.existsById(99L)).thenReturn(false);

        boolean result = userService.delete(99L);

        assertThat(result).isFalse();
        verify(userRepository, never()).deleteById(any());
    }
}
