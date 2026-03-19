package WiseFox.Finance.dto.response;

import WiseFox.Finance.model.UserLedger.Permission;

public record UserLedgerResponse(
    Long id,
    Long userId,
    String username,
    String userEmail,
    Long ledgerId,
    String ledgerName,
    Permission permission
) {}