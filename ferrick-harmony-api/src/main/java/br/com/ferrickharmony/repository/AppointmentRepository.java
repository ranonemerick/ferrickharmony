package br.com.ferrickharmony.repository;

import br.com.ferrickharmony.enums.AppointmentStatus;
import br.com.ferrickharmony.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.UUID;

    public interface AppointmentRepository extends JpaRepository<Appointment, UUID>, JpaSpecificationExecutor<Appointment> {
        boolean existsByProfessionalIdAndAppointmentDateAndStatusNot(UUID professionalId, LocalDateTime appointmentDate, AppointmentStatus status);
        Page<Appointment> findByPatientId(UUID patientId, Pageable pageable);
        Page<Appointment> findByProfessionalId(UUID professionalId, Pageable pageable);
        Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);
    }
