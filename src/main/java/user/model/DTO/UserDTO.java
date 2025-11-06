package user.model.DTO;

import user.model.enums.Role;

public record UserDTO(Long id, String name, String email, Role role) {
}
