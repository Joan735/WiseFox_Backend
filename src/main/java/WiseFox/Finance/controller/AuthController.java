package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import WiseFox.Finance.model.User;
import WiseFox.Finance.service.AuthService;

import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/auth") // from url starting with
public class AuthController {
	@Autowired
	private AuthService authService;

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
			User createdUser = authService.register(user);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// Login User
	// POST /api/auth/login
	@PostMapping("/login")
	public ResponseEntity<User> loginUser(@RequestBody User user) {
		User loginUser = authService.login(user.getEmail(), user.getPassword());

		if (loginUser == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		} else {
			return ResponseEntity.status(HttpStatus.CREATED).body(loginUser);
		}
	}
}