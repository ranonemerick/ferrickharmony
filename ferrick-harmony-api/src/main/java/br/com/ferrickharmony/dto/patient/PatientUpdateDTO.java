package br.com.ferrickharmony.dto.patient;

import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record PatientUpdateDTO(
        String name,

        @Email(message = "{error.patient.email.invalid}")
        String email,

        LocalDate birthDate,
        String phone,
        String secondaryPhone,
        String cep,
        String street,
        String number,
        String complement,
        String neighborhood,
        String city,
        String state,
        Boolean active
) {}