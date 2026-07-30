package br.com.ferrickharmony.dto.patient;

import br.com.ferrickharmony.model.Address;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientResponseDTO(
        UUID id,
        String name,
        String cpf,
        String email,
        LocalDate birthDate,
        String phone,
        String secondaryPhone,
        Address address,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
