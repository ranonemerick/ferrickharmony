package br.com.ferrickharmony.dto.user;

import br.com.ferrickharmony.enums.UserRole;
import jakarta.validation.constraints.Email;

public record UserUpdateDTO(@Email(message = "Invalid email format")
                            String email,
                            UserRole role,
                            Boolean active) {
}
