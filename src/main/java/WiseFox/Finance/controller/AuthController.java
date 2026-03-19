package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.dto.mapper.AuthMapper;
import WiseFox.Finance.dto.request.AuthRequest;
import WiseFox.Finance.dto.response.AuthResponse;
import WiseFox.Finance.model.User;
import WiseFox.Finance.service.AuthService;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    // Register
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
                System.err.println("Error: Enter all the data");
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
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                if (pfpFile.getSize() > 5 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                user.setPfp(pfpFile.getBytes());
            }
            
            User createdUser = authService.register(user);
            AuthResponse response = AuthMapper.toResponse(createdUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (ResponseStatusException e) {
            System.err.println("Register Error: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IOException e) {
            System.err.println("File processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.err.println("Register Error:" + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@RequestBody AuthRequest request) {
        try {
            User loginUser = authService.login(request.getEmail(), request.getPassword());
            AuthResponse response = AuthMapper.toResponse(loginUser);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            System.err.println("Login Error: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            System.err.println("Login Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}