package br.com.ferrickharmony.dto.user;

import br.com.ferrickharmony.enums.UserRole;
import jakarta.validation.constraints.Email;

public record UserUpdateDTO(
        @Email(message = "{error.user.email.invalid}")
        String email,
        UserRole role,
        Boolean active
) {}
