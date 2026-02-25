package WiseFox.Finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.AuthRepository;

@Service
public class AuthService {
	@Autowired
	private AuthRepository authRepository;

	// REGISTER
	public User register(User user) {
		return authRepository.save(user);
	}

	// LOGIN
	public User login(String email, String password) {
		return authRepository.existsByEmailAndPassword(email, password).orElse(null);
	}
}