package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.dto.mapper.AuthMapper;
import WiseFox.Finance.dto.request.AuthRequest;
import WiseFox.Finance.dto.request.GoogleAuthRequest;
import WiseFox.Finance.dto.request.GoogleRegisterRequest;
import WiseFox.Finance.dto.request.VerifyCodeRequest;
import WiseFox.Finance.dto.response.AuthResponse;
import WiseFox.Finance.model.User;
import WiseFox.Finance.service.AuthService;
import WiseFox.Finance.service.GoogleAuthService;
import WiseFox.Finance.service.JwtService;

import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private JwtService jwtService;

    // -------------------------------------------------------------------------
    // Standard register
    // -------------------------------------------------------------------------
    @PostMapping(value = "/register", consumes = {"multipart/form-data"})
    public ResponseEntity<AuthResponse> registerUser(
            @RequestParam("name") String name,
            @RequestParam("surname") String surname,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "pfpFile", required = false) MultipartFile pfpFile) {

        try {
            if (StringUtils.isAnyBlank(name, surname, username, email, password)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            User user = new User();
            user.setName(name);
            user.setSurname(surname);
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password);

            if (pfpFile != null && !pfpFile.isEmpty()) {
                String contentType = pfpFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                if (pfpFile.getSize() > 5 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }
                user.setPfp(pfpFile.getBytes());
            }

            User createdUser = authService.register(user);
            String token = jwtService.generateToken(createdUser.getId(), createdUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(AuthMapper.toResponse(createdUser, token));

        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // -------------------------------------------------------------------------
    // Standard login
    // -------------------------------------------------------------------------
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest request) {
        User loginUser = authService.login(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(loginUser.getId(), loginUser.getEmail());
        return ResponseEntity.ok(AuthMapper.toResponse(loginUser, token));
    }

    // -------------------------------------------------------------------------
    // GOOGLE STEP 1 — Frontend sends Google ID token
    // Response A: { "status": "OK",              "token": "<jwt>" }
    // Response B: { "status": "VERIFY_REQUIRED", "email": "user@..." }
    // -------------------------------------------------------------------------
    @PostMapping("/google")
    public ResponseEntity<Map<String, String>> googleLogin(
            @Valid @RequestBody GoogleAuthRequest request) {
        Map<String, String> result = googleAuthService.handleGoogleLogin(request.getIdToken());
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // GOOGLE STEP 2 — Frontend submits the 6-digit code
    // Response: { "googleToken": "<short-lived-token>" }
    // -------------------------------------------------------------------------
    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, String>> verifyCode(
            @Valid @RequestBody VerifyCodeRequest request) {
        Map<String, String> result = googleAuthService.verifyCode(
                request.getEmail(), request.getCode());
        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // GOOGLE STEP 3 — Frontend submits register form + googleToken
    // Response: { "token": "<jwt>" }
    // -------------------------------------------------------------------------
    @PostMapping("/register/google")
    public ResponseEntity<Map<String, String>> registerWithGoogle(
            @Valid @RequestBody GoogleRegisterRequest request) {
        Map<String, String> result = googleAuthService.registerWithGoogle(
                request.getGoogleToken(),
                request.getUsername(),
                request.getName(),
                request.getSurname(),
                request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}