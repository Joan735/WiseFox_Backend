package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.dto.mapper.UserMapper;
import WiseFox.Finance.dto.request.UserUpdateRequest;
import WiseFox.Finance.dto.response.UserResponse;
import WiseFox.Finance.model.User;
import WiseFox.Finance.service.UserService;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("surname") String surname,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "pfpFile", required = false) MultipartFile pfpFile) {
        
        if (StringUtils.isAnyBlank(name, surname, username, email, password)) {
            System.err.println("Error: Enter all the data");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        
        try {
            User existingUser = userService.getById(id);
            
            existingUser.setName(name);
            existingUser.setSurname(surname);
            existingUser.setUsername(username);
            existingUser.setEmail(email);
            existingUser.setPassword(password);
            
            if (role != null) {
                existingUser.setRole(User.Role.valueOf(role));
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok().build();
    }

    // Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            UserResponse response = UserMapper.toResponse(user);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            System.err.println("Error Get by id: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            System.err.println("Error Get by id: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get by Username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getByUsername(username);
            UserResponse response = UserMapper.toResponse(user);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            System.err.println("Error Get by username: " + e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            System.err.println("Error Get by username: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/{id}/pfp")
    public ResponseEntity<byte[]> getUserProfilePicture(@PathVariable Long id) {
        try {
            User user = userService.getById(id);
            byte[] pfp = user.getPfp();
            
            if (pfp == null || pfp.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(pfp);
                
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }
}