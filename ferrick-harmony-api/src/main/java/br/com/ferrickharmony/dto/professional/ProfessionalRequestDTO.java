package br.com.ferrickharmony.dto.professional;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfessionalRequestDTO(
        @NotBlank(message = "{error.professional.name.required}")
        String name,

        @NotBlank(message = "{error.professional.cpf.required}")
        @Size(min = 11, max = 11, message = "{error.professional.cpf.size}")
        String cpf,

        @NotBlank(message = "{error.professional.document.required}")
        String document,

        @NotBlank(message = "{error.professional.email.required}")
        @Email(message = "{error.professional.email.invalid}")
        String email,

        @NotBlank(message = "{error.professional.phone.required}")
        String phone
) {}
