package WiseFox.Finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.AuthRepository;

@Service
public class AuthService {
	@Autowired
	private AuthRepository authRepository;

	// REGISTER
	@Transactional
	public User register(User user) {
		if (authRepository.existsByUsername(user.getUsername())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The Username is already used.");
		}
		if (authRepository.existsByEmail(user.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The Email is already used.");
		}
		return authRepository.save(user);
	}

	// LOGIN
	public User login(String email, String password) {
	    return authRepository.findByEmailAndPassword(email, password)
	        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password."));
	}
}