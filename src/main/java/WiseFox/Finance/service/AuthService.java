package WiseFox.Finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.FinanceRepository;

@Service
public class AuthService {
	@Autowired
	private FinanceRepository financeRepository;

	// REGISTER
	public User register(User user) {
		return financeRepository.save(user);
	}

	// LOGIN
	public User login(String email, String password) {
		return financeRepository.existsByEmailAndPassword(email, password).orElse(null);
	}
}