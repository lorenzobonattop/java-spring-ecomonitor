package user.model.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import user.model.enums.Role;

public record UserCreateRequest (
        @NotBlank(message = "Name is mandatory") String name,
        @NotBlank(message = "Email is mandatory") String email,
        @Size(min = 6, message = "Senha é obrigatório") String password,
        @NotNull(message = "Cargo é obrigatório")Role role
        )
{}
