package WiseFox.Finance.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.FinanceRepository;

@Service
public class UserService {
	@Autowired
	private FinanceRepository financeRepository;

	// UPDATE
	public User update(Long id, User userData) {
		Optional<User> existingUser = financeRepository.findById(id);

		if (existingUser.isPresent()) {
			User user = existingUser.get();
			user.setName(userData.getName());
			user.setSurname(userData.getSurname());
			user.setUsername(userData.getUsername());
			user.setEmail(userData.getEmail());
			user.setPassword(userData.getPassword());
			user.setRole(userData.getRole());
			user.setPfp(userData.getPfp());
			return financeRepository.save(user);
		}
		return null;
	}

	// DELETE
	public boolean delete(Long id) {
		if (financeRepository.existsById(id)) {
			financeRepository.deleteById(id);
			return true;
		}
		return false;
	}

	// GET BY ID
	public User getById(Long id) {
		return financeRepository.findById(id).orElse(null);
	}

	// GET BY USERNAME
	public User getByUsername(String username) {
		if (username == null || username.isEmpty()) {
			return null; // Returns null if username is null or empty
		}
		String lowerUsername = username.toLowerCase();
		return financeRepository.findByUsernameIgnoreCase(lowerUsername).orElse(null); // Returns null if no match is found
	}
}