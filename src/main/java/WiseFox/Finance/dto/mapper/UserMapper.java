// WiseFox.Finance.dto.mapper.UserMapper.java
package WiseFox.Finance.dto.mapper;

import WiseFox.Finance.dto.request.UserUpdateRequest;
import WiseFox.Finance.dto.response.UserResponse;
import WiseFox.Finance.model.User;

public class UserMapper {
    
    public static UserResponse toResponse(User user) {
        if (user == null) return null;
        
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getSurname(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getPfp() != null && user.getPfp().length > 0
        );
    }
    
    public static void updateEntityFromRequest(User user, UserUpdateRequest request) {
        if (request == null || user == null) return;
        
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole());
        user.setPfp(request.getPfp());
    }
}