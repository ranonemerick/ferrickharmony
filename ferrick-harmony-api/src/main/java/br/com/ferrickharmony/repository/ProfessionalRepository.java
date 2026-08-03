package br.com.ferrickharmony.repository;

import br.com.ferrickharmony.model.Professional;
import io.micrometer.observation.ObservationFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String sanitizedEmail);
    Page<Professional> findAllByActiveTrue(Pageable pageable);
    Optional<Professional> findByCpf(String cpf);
}
