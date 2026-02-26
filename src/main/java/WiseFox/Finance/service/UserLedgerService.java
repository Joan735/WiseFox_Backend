package WiseFox.Finance.service;

import WiseFox.Finance.repository.UserLedgerRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import WiseFox.Finance.model.UserLedger;

@Service
public class UserLedgerService {
	@Autowired
	private UserLedgerRepository userLedgerRepository;

	// CREATE
	public UserLedger create(UserLedger userLedger) {
		return userLedgerRepository.save(userLedger);
	}

	// SHARE
	public UserLedger share(UserLedger userledger) {
		return userLedgerRepository.save(userledger);
	}
}
