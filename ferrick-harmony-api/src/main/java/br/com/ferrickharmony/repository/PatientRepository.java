package br.com.ferrickharmony.repository;

import br.com.ferrickharmony.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String sanitizedEmail);
}
