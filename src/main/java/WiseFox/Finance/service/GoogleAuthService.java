package WiseFox.Finance.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.EmailVerificationCode;
import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.AuthRepository;
import WiseFox.Finance.repository.EmailVerificationCodeRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private EmailVerificationCodeRepository verificationCodeRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // -----------------------------------------------------------------------
    // STEP 1 — Frontend sends Google ID token
    // Returns:
    //   Existing user → { status, token, userId, username, email, name, surname, role }
    //   New user      → { status: "VERIFY_REQUIRED", email }
    // -----------------------------------------------------------------------
    @Transactional
    public Map<String, String> handleGoogleLogin(String idTokenString) {
        GoogleIdToken.Payload payload = verifyGoogleToken(idTokenString);
        String email = payload.getEmail();

        if (authRepository.existsByEmail(email)) {
            User user = authRepository.findByEmail(email).get();
            String jwt = jwtService.generateToken(user.getId(), user.getEmail());

            // ✅ Return full user info so Android can save the ID
            Map<String, String> result = new HashMap<>();
            result.put("status",   "OK");
            result.put("token",    jwt);
            result.put("userId",   String.valueOf(user.getId()));
            result.put("username", user.getUsername());
            result.put("email",    user.getEmail());
            result.put("name",     user.getName());
            result.put("surname",  user.getSurname());
            result.put("role",     user.getRole().name());
            return result;
        } else {
            sendVerificationCode(email);
            Map<String, String> result = new HashMap<>();
            result.put("status", "VERIFY_REQUIRED");
            result.put("email",  email);
            return result;
        }
    }

    // -----------------------------------------------------------------------
    // STEP 2 — Frontend submits the 6-digit code
    // Returns: { googleToken }
    // -----------------------------------------------------------------------
    @Transactional
    public Map<String, String> verifyCode(String email, String code) {
        EmailVerificationCode record = verificationCodeRepository
                .findTopByEmailOrderByExpiresAtDesc(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "No verification code found for this email."));

        if (record.isUsed()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This code has already been used.");
        }
        if (LocalDateTime.now().isAfter(record.getExpiresAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification code has expired.");
        }
        if (!record.getCode().equals(code)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect verification code.");
        }

        record.setUsed(true);
        verificationCodeRepository.save(record);

        String googleToken = jwtService.generateGoogleRegistrationToken(email);

        Map<String, String> result = new HashMap<>();
        result.put("googleToken", googleToken);
        return result;
    }

    // -----------------------------------------------------------------------
    // STEP 3 — Frontend submits registration form + googleToken
    // Returns: { token, userId, username, email, name, surname, role }
    // -----------------------------------------------------------------------
    @Transactional
    public Map<String, String> registerWithGoogle(
            String googleToken, String username, String name, String surname, String password) {

        String email = jwtService.getEmailFromGoogleRegistrationToken(googleToken);

        if (authRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered.");
        }
        if (authRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username is already taken.");
        }

        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setSurname(surname);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.USER);

        User saved = authRepository.save(user);

        verificationCodeRepository.deleteByEmail(email);

        String jwt = jwtService.generateToken(saved.getId(), saved.getEmail());

        // ✅ Return full user info so Android can save the ID after registration
        Map<String, String> result = new HashMap<>();
        result.put("token",    jwt);
        result.put("userId",   String.valueOf(saved.getId()));
        result.put("username", saved.getUsername());
        result.put("email",    saved.getEmail());
        result.put("name",     saved.getName());
        result.put("surname",  saved.getSurname());
        result.put("role",     saved.getRole().name());
        return result;
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------
    private void sendVerificationCode(String email) {
        verificationCodeRepository.deleteByEmail(email);

        String code = String.format("%06d", new Random().nextInt(999999));

        EmailVerificationCode record = new EmailVerificationCode();
        record.setEmail(email);
        record.setCode(code);
        record.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.save(record);

        emailService.sendVerificationCode(email, code);
    }

    private GoogleIdToken.Payload verifyGoogleToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Google ID token.");
            }
            return idToken.getPayload();
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Failed to verify Google token: " + e.getMessage());
        }
    }
}