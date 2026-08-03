package br.com.ferrickharmony.dto.user;

import br.com.ferrickharmony.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "{error.user.email.required}")
        @Email(message = "{error.user.email.invalid}")
        String email,

        @NotBlank(message = "{error.user.password.required}")
        @Size(min = 6, message = "{error.user.password.size}")
        String password,

        @NotNull(message = "{error.user.role.required}")
        UserRole role,

        boolean active
) {}
