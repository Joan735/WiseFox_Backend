package WiseFox.Finance.dto.response;

public record LedgerResponse(
    Long id,
    String name,
    String currency,
    String description,
    Long ownerId,
    String ownerUsername
) {}