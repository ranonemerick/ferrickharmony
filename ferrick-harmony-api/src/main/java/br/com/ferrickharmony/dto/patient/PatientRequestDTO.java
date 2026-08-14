package br.com.ferrickharmony.dto.patient;

import br.com.ferrickharmony.validation.cpf.ValidCPF;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientRequestDTO(
        @NotBlank(message = "{error.patient.name.required}")
        String name,

        @NotBlank(message = "{error.patient.cpf.required}")
        @Size(min = 11, max = 11, message = "{error.patient.cpf.size}")
        @ValidCPF(message = "{error.patient.cpf.invalid}")
        String cpf,

        @Email(message = "{error.patient.email.invalid}")
        String email,

        @NotNull(message = "{error.patient.birthDate.required}")
        LocalDate birthDate,

        @NotBlank(message = "{error.patient.phone.required}")
        String phone,

        String secondaryPhone,

        @NotBlank(message = "{error.patient.cep.required}")
        String cep,

        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state
) {}