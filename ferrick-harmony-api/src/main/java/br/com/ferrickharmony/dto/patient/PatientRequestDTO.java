package br.com.ferrickharmony.dto.patient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequestDTO(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "CPF is required")
        @Size(min = 11, max = 11, message = "CPF must have exactly 11 digits")
        String cpf,

        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Birth date is required")
        LocalDate birthDate,

        @NotBlank(message = "Phone is required")
        String phone,

        String secondaryPhone,

        @NotBlank(message = "ZIP code is required")
        String cep,

        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state
) {}