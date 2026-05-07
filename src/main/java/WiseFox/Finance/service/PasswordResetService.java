package WiseFox.Finance.service;

import WiseFox.Finance.model.EmailVerificationCode;
import WiseFox.Finance.model.User;
import WiseFox.Finance.repository.AuthRepository;
import WiseFox.Finance.repository.EmailVerificationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
public class PasswordResetService {

	@Autowired
	private AuthRepository authRepository;
	@Autowired
	private EmailVerificationCodeRepository verificationCodeRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JwtService jwtService;

	// STEP 1 — Verify email and send code
	@Transactional
	public void sendResetCode(String email) {
		if (!authRepository.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No account found with this email.");
		}

		verificationCodeRepository.deleteByEmail(email);

		String code = String.format("%06d", new Random().nextInt(999999));

		EmailVerificationCode record = new EmailVerificationCode();
		record.setEmail(email);
		record.setCode(code);
		record.setExpiresAt(LocalDateTime.now().plusMinutes(10));
		verificationCodeRepository.save(record);

		emailService.sendPasswordResetCode(email, code);
	}

	// STEP 2 — Verify code, return temporal token 
	@Transactional
	public String verifyResetCode(String email, String code) {
		EmailVerificationCode record = verificationCodeRepository.findTopByEmailOrderByExpiresAtDesc(email).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "No reset code found for this email."));

		if (record.isUsed())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This code has already been used.");
		if (LocalDateTime.now().isAfter(record.getExpiresAt()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code has expired.");
		if (!record.getCode().equals(code))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect code.");

		record.setUsed(true);
		verificationCodeRepository.save(record);

		// Returns a temporal token (reuses generateGoogleRegistrationToken)
		return jwtService.generateGoogleRegistrationToken(email);
	}

	// STEP 3 — Change password using the temporal token
	@Transactional
	public void resetPassword(String resetToken, String newPassword) {
		String email = jwtService.getEmailFromGoogleRegistrationToken(resetToken);

		User user = authRepository.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found."));

		user.setPassword(passwordEncoder.encode(newPassword));
		authRepository.save(user);

		verificationCodeRepository.deleteByEmail(email);
	}
}