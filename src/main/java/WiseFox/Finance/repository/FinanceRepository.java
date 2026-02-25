package WiseFox.Finance.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import WiseFox.Finance.model.User;

@Repository
public interface FinanceRepository extends CrudRepository<User, Long> {
	Optional<User> existsByEmailAndPassword(String email, String password);
	
	Optional<User> findByUsernameIgnoreCase(String username);
}