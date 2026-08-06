package br.com.ferrickharmony.dto.appointment;

import br.com.ferrickharmony.enums.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponseDTO(
        UUID id,
        UUID patientId,
        String patientName,
        UUID professionalId,
        String professionalName,
        LocalDateTime appointmentDate,
        String location,
        AppointmentStatus status,
        String notes,
        LocalDateTime createdAt
) {}