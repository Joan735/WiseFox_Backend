package WiseFox.Finance.dto.mapper;

import WiseFox.Finance.dto.request.UserLedgerRequest;
import WiseFox.Finance.dto.response.UserLedgerResponse;
import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;
import WiseFox.Finance.model.UserLedger;

public class UserLedgerMapper {
    
    public static UserLedgerResponse toResponse(UserLedger userLedger) {
        if (userLedger == null) return null;
        
        Long userId = null;
        String username = null;
        String userEmail = null;
        
        Long ledgerId = null;
        String ledgerName = null;
        
        if (userLedger.getUser() != null) {
            userId = userLedger.getUser().getId();
            username = userLedger.getUser().getUsername();
            userEmail = userLedger.getUser().getEmail();
        }
        
        if (userLedger.getLedger() != null) {
            ledgerId = userLedger.getLedger().getId();
            ledgerName = userLedger.getLedger().getName();
        }
        
        return new UserLedgerResponse(
            userLedger.getId(),
            userId,
            username,
            userEmail,
            ledgerId,
            ledgerName,
            userLedger.getPermission()
        );
    }
    
    public static UserLedger toEntity(UserLedgerRequest request, User user, Ledger ledger) {
        if (request == null) return null;
        
        UserLedger userLedger = new UserLedger();
        userLedger.setUser(user);
        userLedger.setLedger(ledger);
        return userLedger;
    }
}