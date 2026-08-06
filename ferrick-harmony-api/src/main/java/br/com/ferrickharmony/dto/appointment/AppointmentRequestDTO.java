package br.com.ferrickharmony.dto.appointment;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentRequestDTO(
        @NotNull(message = "{error.appointment.patient.required}")
        UUID patientId,

        @NotNull(message = "{error.appointment.professional.required}")
        UUID professionalId,

        @NotNull(message = "{error.appointment.date.required}")
        @Future(message = "{error.appointment.date.future}")
        LocalDateTime appointmentDate,

        String location,
        String notes
) {}