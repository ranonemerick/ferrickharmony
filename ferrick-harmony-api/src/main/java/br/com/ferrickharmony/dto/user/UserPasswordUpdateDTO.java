package br.com.ferrickharmony.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPasswordUpdateDTO(
        @NotBlank(message = "{error.user.password.required}")
        @Size(min = 6, message = "{error.user.password.size}")
        String password
) {}
