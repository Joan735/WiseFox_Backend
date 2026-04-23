package WiseFox.Finance.dto.mapper;

import WiseFox.Finance.dto.request.AuthRequest;
import WiseFox.Finance.dto.response.AuthResponse;
import WiseFox.Finance.model.User;

public class AuthMapper {

    public static AuthResponse toResponse(User user) {
        if (user == null) return null;

        return new AuthResponse(
            user.getId(),
            user.getName(),
            user.getSurname(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            null,
            "Success"
        );
    }

    public static AuthResponse toResponse(User user, String token) {
        if (user == null) return null;

        return new AuthResponse(
            user.getId(),
            user.getName(),
            user.getSurname(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            token,
            "Success"
        );
    }

    public static User toEntity(AuthRequest request) {
        if (request == null) return null;

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setUsername(request.getUsername());
        return user;
    }
}