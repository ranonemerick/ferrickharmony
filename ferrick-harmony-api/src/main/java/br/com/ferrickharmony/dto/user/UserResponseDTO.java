package br.com.ferrickharmony.dto.user;

import br.com.ferrickharmony.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDTO(
        UUID id,
        String email,
        UserRole role,
        boolean active,
        LocalDateTime createdAt
) {}
