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
		if (authRepository.existsByEmail(user.getEmail()) || authRepository.existsByUsername(user.getUsername())) {
			System.err.println("ERROR: Duplicated Username or Email.");
			throw new ResponseStatusException(HttpStatus.CONFLICT, "The Username or Email is already used.");
		}
		return authRepository.save(user);
	}

	// LOGIN
	public User login(String email, String password) {
		return authRepository.findByEmailAndPassword(email, password).orElse(null);
	}
}