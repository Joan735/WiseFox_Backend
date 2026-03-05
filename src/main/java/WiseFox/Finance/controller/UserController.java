package WiseFox.Finance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.User;
import WiseFox.Finance.service.UserService;

import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/user") // from url starting with
public class UserController {
	@Autowired
	private UserService userService;

	// Update User by ID
	// PUT /api/user/{id}
	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
		// Verify that the data submitted is correct
		if (StringUtils.isAnyBlank(user.getName(), user.getSurname(), user.getUsername(), user.getEmail(),
				user.getPassword())) {
			System.err.println("Error: Enter all the data");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		try {
	        User updatedUser = userService.update(id, user);
			return ResponseEntity.ok(updatedUser);
	    } catch (ResponseStatusException e) {
	        System.err.println("Error Create: " + e);
	        return ResponseEntity.status(e.getStatusCode()).build();
	    } catch (Exception e) {
	        System.err.println("Error Create: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	// Delete User by ID
	// DELETE /api/user/{id}
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		boolean deleted = userService.delete(id);
		if (!deleted) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok().build();
	}

	// Get User by ID
	// GET /api/user/{id}
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id) {
	    try {
	        User user = userService.getById(id);
	        return ResponseEntity.ok(user);
	    } catch (ResponseStatusException e) {
	        System.err.println("Error Get by id: " + e);
	        return ResponseEntity.status(e.getStatusCode()).build();
	    } catch (Exception e) {
	        System.err.println("Error Get by id: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}

	// Get User by Username
	// GET /api/user/username/{username}
	@GetMapping("/username/{username}")
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
		try {
			User user = userService.getByUsername(username);
			return ResponseEntity.ok(user);
	    } catch (ResponseStatusException e) {
	        System.err.println("Error Get by username: " + e);
	        return ResponseEntity.status(e.getStatusCode()).build();
	    } catch (Exception e) {
	        System.err.println("Error Get by username: " + e.getMessage());
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	    }
	}
}