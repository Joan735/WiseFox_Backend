package WiseFox.Finance.dto.mapper;

import WiseFox.Finance.dto.request.LedgerRequest;
import WiseFox.Finance.dto.response.LedgerResponse;
import WiseFox.Finance.model.Ledger;
import WiseFox.Finance.model.User;

public class LedgerMapper {
    
	public static LedgerResponse toResponse(Ledger ledger, int memberCount) {
	    if (ledger == null) return null;
	    Long ownerId = null;
	    String ownerUsername = null;
	    if (ledger.getUser() != null) {
	        ownerId = ledger.getUser().getId();
	        ownerUsername = ledger.getUser().getUsername();
	    }
	    return new LedgerResponse(
	        ledger.getId(),
	        ledger.getName(),
	        ledger.getCurrency(),
	        ledger.getDescription(),
	        ownerId,
	        ownerUsername,
	        memberCount
	    );
	}
	
	public static LedgerResponse toResponse(Ledger ledger) {
	    return toResponse(ledger, 0);
	}
    
    public static Ledger toEntity(LedgerRequest request, User user) {
        if (request == null) return null;
        
        Ledger ledger = new Ledger();
        ledger.setName(request.getName());
        ledger.setCurrency(request.getCurrency());
        ledger.setDescription(request.getDescription());
        ledger.setUser(user);
        return ledger;
    }
    
    public static void updateEntityFromRequest(Ledger ledger, LedgerRequest request) {
        if (request == null || ledger == null) return;
        
        ledger.setName(request.getName());
        ledger.setCurrency(request.getCurrency());
        ledger.setDescription(request.getDescription());
    }
}