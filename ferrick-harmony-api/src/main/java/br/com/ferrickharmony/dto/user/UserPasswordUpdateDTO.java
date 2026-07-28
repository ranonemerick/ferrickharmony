package br.com.ferrickharmony.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateDTO(
        @NotBlank(message = "The new password is required")
        @Size(min = 6, message = "The password must have at least 6 characters")
        String password
) {}
