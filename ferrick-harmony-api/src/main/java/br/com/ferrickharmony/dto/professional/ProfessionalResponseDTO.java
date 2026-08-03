package br.com.ferrickharmony.dto.professional;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProfessionalResponseDTO(
        UUID id,
        String name,
        String cpf,
        String document,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        boolean active
) {}
