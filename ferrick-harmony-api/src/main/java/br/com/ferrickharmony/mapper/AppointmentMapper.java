package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.appointment.AppointmentRequestDTO;
import br.com.ferrickharmony.dto.appointment.AppointmentResponseDTO;
import br.com.ferrickharmony.model.Appointment;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequestDTO dto) {
        if (dto == null) return null;

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(dto.appointmentDate());
        appointment.setLocation(dto.location());
        appointment.setNotes(dto.notes());
        return appointment;
    }

    public AppointmentResponseDTO toResponseDTO(Appointment entity) {
        if (entity == null) return null;

        return new AppointmentResponseDTO(
                entity.getId(),
                entity.getPatient() != null ? entity.getPatient().getId() : null,
                entity.getPatient() != null ? entity.getPatient().getName() : null,
                entity.getProfessional() != null ? entity.getProfessional().getId() : null,
                entity.getProfessional() != null ? entity.getProfessional().getName() : null,
                entity.getAppointmentDate(),
                entity.getLocation(),
                entity.getStatus(),
                entity.getNotes(),
                entity.getCreatedAt()
        );
    }
}