package br.com.ferrickharmony.dto.email;

import java.util.Map;
import java.util.UUID;

public record EmailDTO(
        UUID userId,
        String emailTo,
        String subject,
        String text,
        String appointmentEmail,
        Map<String, Object> variables
) {}
