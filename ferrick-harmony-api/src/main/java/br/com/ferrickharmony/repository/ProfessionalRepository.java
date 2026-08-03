package br.com.ferrickharmony.repository;

import br.com.ferrickharmony.model.Professional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfessionalRepository extends JpaRepository<Professional, UUID> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String sanitizedEmail);
}
