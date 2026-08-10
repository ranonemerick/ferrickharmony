package br.com.ferrickharmony.dto.patient;

public record PatientFilterDTO(
        String name,
        String cpf,
        String email,
        String phone
) {}
