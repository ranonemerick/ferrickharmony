package br.com.ferrickharmony.dto.appointment;

import br.com.ferrickharmony.enums.AppointmentStatus;
import java.time.LocalDateTime;

public record AppointmentFilterDTO(
        String patientName,
        String professionalName,
        LocalDateTime startDate,
        LocalDateTime endDate,
        AppointmentStatus status
) {}