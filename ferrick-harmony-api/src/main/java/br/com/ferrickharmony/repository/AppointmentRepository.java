package br.com.ferrickharmony.repository;

import br.com.ferrickharmony.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

}
