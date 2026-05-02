package WiseFox.Finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.UserRepository;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// UPDATE
	@Transactional
	public User update(Long id, User userDetails) {
		return userRepository.findById(id).map(user -> {
			user.setName(userDetails.getName());
			user.setSurname(userDetails.getSurname());
			user.setUsername(userDetails.getUsername());
			user.setEmail(userDetails.getEmail());

			String newPassword = userDetails.getPassword();
			if (newPassword != null && !newPassword.isBlank()) {
				if (!newPassword.startsWith("$2a$") && !newPassword.startsWith("$2b$")) {
					user.setPassword(passwordEncoder.encode(newPassword));
				} else {
					user.setPassword(newPassword);
				}
			}
			// blank/null → password column left unchanged

			if (userDetails.getRole() != null) {
				user.setRole(userDetails.getRole());
			}

			if (userDetails.getPfp() != null) {
				user.setPfp(userDetails.getPfp());
			}

			return userRepository.save(user);
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cannot update: User not found"));
	}

	// DELETE
	public boolean delete(Long id) {
		if (userRepository.existsById(id)) {
			userRepository.deleteById(id);
			return true;
		}
		return false;
	}

	// GET BY ID
	public User getById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + id));
	}

	// GET BY USERNAME
	public User getByUsername(String username) {
		return userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with username: " + username));
	}
}