package WiseFox.Finance.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import WiseFox.Finance.model.UserLedger;
import WiseFox.Finance.service.UserLedgerService;

import org.apache.commons.lang3.StringUtils;

@RestController
@RequestMapping("/api/userledger") // from url starting with
public class UserLedgerController {
	@Autowired
	private UserLedgerService userledgerService;

	// Create user ledger
	// POST /api/userledger/create
	@PostMapping("/create")
	public ResponseEntity<UserLedger> createUserLedger(@RequestBody UserLedger userledger) {
		try {
			// Basic validation
			if (StringUtils.isAnyBlank(userledger.getUser().getName(), userledger.getLedger().getName())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			UserLedger createdUserLedger = userledgerService.create(userledger);
			return ResponseEntity.status(HttpStatus.CREATED).body(createdUserLedger);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	// Share Ledger
	// POST /api/userledger/share
	@PostMapping("/share")
	public ResponseEntity<UserLedger> shareLedger(@RequestBody UserLedger userledger) {
		
		return ResponseEntity.ok(userledgerService.share(userledger));
	}
}