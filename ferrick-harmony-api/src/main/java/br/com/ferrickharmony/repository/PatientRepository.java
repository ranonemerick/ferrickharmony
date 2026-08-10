package br.com.ferrickharmony.repository;

import br.com.ferrickharmony.model.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID>, JpaSpecificationExecutor<Patient> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String sanitizedEmail);
    boolean existsByEmailAndIdNot(String sanitizedEmail, UUID id);
    Page<Patient> findAllByActiveTrue(Pageable pageable);
    Optional<Patient> findByCpf(String cpf);
}