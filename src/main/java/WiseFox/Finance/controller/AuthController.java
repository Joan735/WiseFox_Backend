package WiseFox.Finance.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import WiseFox.Finance.model.User;
import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/auth") // from url starting with
public class AuthController {
	// @Autowired
	// private AuthService authService;

	// Register User
	// POST /api/auth/register
	@PostMapping("/register")
	public ResponseEntity<User> registerUser(@RequestBody User user) {
		try {
			// Basic validation
			if (StringUtils.isAnyBlank(user.getName(), user.getSurname(), user.getUsername(), user.getEmail(),
					user.getPassword())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			User createdUser = null; // authService.register(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// Login User
	// POST /api/auth/login
	@PostMapping("/login")
	public ResponseEntity<User> loginUser(@RequestBody User user) {
		User loginUser = null; // authService.login(user.getEmail(), user.getPassword());

		if (StringUtils.isAnyBlank(loginUser.getName(), loginUser.getSurname(), loginUser.getUsername(),
				loginUser.getEmail(), loginUser.getPassword())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(loginUser);
		}
	}

	// Update User by ID
	// PUT /api/auth/{id}
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
		// Verify that the data submitted is correct
		if (StringUtils.isAnyBlank(user.getName(), user.getSurname(), user.getUsername(), user.getEmail(),
				user.getPassword())) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}

		User updatedUser = null; // authService.update(id, nurse);
		if (updatedUser == null) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(updatedUser);
	}

	// Delete User by ID
	// DELETE /api/auth/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		boolean deleted = false; // authService.delete(id);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok().build();
	}

	// Get User by ID
	// GET /api/auth/{id}
	@GetMapping("/{id}")
	public ResponseEntity<Optional<User>> getUserById(@PathVariable Long id) {
		Optional<User> user = null; // authService.getById(id);
		if (user.isPresent()) {
			return ResponseEntity.ok(user);
		}
		return ResponseEntity.notFound().build();
	}

	// Get User by Username
	// GET /api/auth/username/{username}
	@GetMapping("/username/{username}")
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
		User user = null; // authService.getByUsername(username);
		if (user == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(user);
	}
}