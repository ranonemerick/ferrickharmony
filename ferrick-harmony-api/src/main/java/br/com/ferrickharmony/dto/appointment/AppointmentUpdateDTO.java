package br.com.ferrickharmony.dto.appointment;

import br.com.ferrickharmony.enums.AppointmentStatus;
import jakarta.validation.constraints.Future;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentUpdateDTO(
        @Future(message = "{error.appointment.date.future}")
        LocalDateTime appointmentDate,
        UUID professionalId,
        String location,
        String notes,
        AppointmentStatus status
) {}
