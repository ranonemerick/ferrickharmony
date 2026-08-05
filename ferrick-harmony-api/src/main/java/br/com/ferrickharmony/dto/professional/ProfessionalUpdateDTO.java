package br.com.ferrickharmony.dto.professional;

import jakarta.validation.constraints.Email;

public record ProfessionalUpdateDTO(
        String name,
        String cpf,
        String document,
        @Email(message = "{error.professional.email.invalid}")
        String email,
        String phone,
        Boolean active
) {}
