package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.dto.mapper.UserMapper;
import WiseFox.Finance.dto.response.UserResponse;
import WiseFox.Finance.model.User;
import WiseFox.Finance.service.UserService;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestPart("name")     String name,
            @RequestPart("surname")  String surname,
            @RequestPart("username") String username,
            @RequestPart("email")    String email,
            @RequestPart(value = "password", required = false) String password,
            @RequestPart(value = "role",     required = false) String role,
            @RequestPart(value = "pfpFile",  required = false) MultipartFile pfpFile) {

        if (name == null || name.isBlank() ||
            surname == null || surname.isBlank() ||
            username == null || username.isBlank() ||
            email == null || email.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            User existingUser = userService.getById(id);

            existingUser.setName(name.trim());
            existingUser.setSurname(surname.trim());
            existingUser.setUsername(username.trim());
            existingUser.setEmail(email.trim());

            if (password != null && !password.isBlank()) {
                existingUser.setPassword(password);
            }

            if (role != null && !role.isBlank()) {
                try {
                    existingUser.setRole(User.Role.valueOf(role));
                } catch (IllegalArgumentException e) {
                    // unknown role — ignore
                }
            }

            if (pfpFile != null && !pfpFile.isEmpty()) {
                String contentType = pfpFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                if (pfpFile.getSize() > 5 * 1024 * 1024) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
                }
                existingUser.setPfp(pfpFile.getBytes());
            }

            User updatedUser = userService.update(id, existingUser);
            UserResponse response = UserMapper.toResponse(updatedUser);
            return ResponseEntity.ok(response);

        } catch (ResponseStatusException e) {
            System.err.println("Error Update: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (IOException e) {
            System.err.println("File processing error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            System.err.println("Error Update: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.delete(id);
        if (!deleted) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            return ResponseEntity.ok(UserMapper.toResponse(user));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getByUsername(username);
            return ResponseEntity.ok(UserMapper.toResponse(user));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}/pfp")
    public ResponseEntity<byte[]> getUserProfilePicture(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            byte[] pfp = user.getPfp();
            if (pfp == null || pfp.length == 0) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.ok().header("Content-Type", "image/jpeg").body(pfp);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}