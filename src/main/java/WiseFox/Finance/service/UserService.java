package WiseFox.Finance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	// UPDATE
	public User update(Long id, User userDetails) {
		return userRepository.findById(id).map(user -> {
			user.setName(userDetails.getName());
			user.setSurname(userDetails.getSurname());
			user.setUsername(userDetails.getUsername());
			user.setEmail(userDetails.getEmail());
			user.setPassword(userDetails.getPassword());
			user.setRole(userDetails.getRole());
			user.setPfp(userDetails.getPfp());
			return userRepository.save(user);
		}).orElse(null);
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
		return userRepository.findById(id).orElse(null);
	}

	// GET BY USERNAME
	public User getByUsername(String username) {
		return userRepository.findByUsernameIgnoreCase(username).orElse(null);
	}
}