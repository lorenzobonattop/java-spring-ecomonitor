package user.model.DTO;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreateRequest (
        @NotBlank(message = "Name is mandatory") String name,
        @NotBlank(message = "Email is mandatory") String email,
        @Size(min = 6, message = "Password is mandatory") String password
        )
{}
