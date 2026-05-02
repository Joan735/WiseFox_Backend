package WiseFox.Finance.dto.response;

import WiseFox.Finance.model.User.Role;

// FIX #4: added passwordHash so the Android client can re-send the existing
// bcrypt hash when updating the profile without changing the password.
// BCrypt hashes are one-way and safe to transmit — they cannot be reversed.
public record UserResponse(
    Long id,
    String name,
    String surname,
    String username,
    String email,
    Role role,
    boolean hasProfilePicture,
    String passwordHash
) {}