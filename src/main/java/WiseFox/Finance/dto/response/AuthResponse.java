package WiseFox.Finance.dto.response;

public record AuthResponse(
    Long id,
    String name,
    String surname,
    String username,
    String email,
    String token, // Reserved for JWT — always null until auth with tokens is implemented
    String message
) {}