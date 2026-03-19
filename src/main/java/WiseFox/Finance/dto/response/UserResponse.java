package WiseFox.Finance.dto.response;

import WiseFox.Finance.model.User.Role;

public record UserResponse(
    Long id,
    String name,
    String surname,
    String username,
    String email,
    Role role,
    boolean hasProfilePicture
) {}