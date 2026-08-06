package br.com.ferrickharmony.repository;

import br.com.ferrickharmony.model.Professional;
import io.micrometer.observation.ObservationFilter;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String sanitizedEmail);
    boolean existsByEmailAndIdNot(String email, UUID id);
    boolean existsByCpfAndIdNot(String cpf, UUID id);
    boolean existsByDocumentAndIdNot(String document, UUID id);
    boolean existsByDocument(String document);
    Page<Professional> findAllByActiveTrue(Pageable pageable);
    Optional<Professional> findByCpf(String cpf);
}
