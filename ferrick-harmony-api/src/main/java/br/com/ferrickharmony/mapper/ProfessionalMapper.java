package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.professional.ProfessionalRequestDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalResponseDTO;
import br.com.ferrickharmony.dto.professional.ProfessionalUpdateDTO;
import br.com.ferrickharmony.model.Professional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ProfessionalMapper {

    public Professional toEntity(ProfessionalRequestDTO dto) {
        if (dto == null) return null;

        return Professional.builder()
                .name(dto.name())
                .cpf(dto.cpf())
                .document(dto.document())
                .email(dto.email())
                .phone(dto.phone())
                .active(true)
                .build();
    }

    public ProfessionalResponseDTO toResponseDTO(Professional entity) {
        if (entity == null) return null;

        return new ProfessionalResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getCpf(),
                entity.getDocument(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.isActive()
        );
    }

    public void updateEntityFromRequest(Professional entity, ProfessionalUpdateDTO dto) {
        if (dto == null || entity == null) {
            return;
        }

        if (StringUtils.hasText(dto.name())) {
            entity.setName(dto.name());
        }

        if (StringUtils.hasText(dto.cpf())) {
            entity.setCpf(dto.cpf());
        }

        if (StringUtils.hasText(dto.document())) {
            entity.setDocument(dto.document());
        }

        if (StringUtils.hasText(dto.phone())) {
            entity.setPhone(dto.phone());
        }

        if (dto.active() != null) {
            entity.setActive(dto.active());
        }
    }
}