package br.com.ferrickharmony.notification.repository;

import br.com.ferrickharmony.notification.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmailRepository extends JpaRepository<Email, UUID> {

}
