package br.com.ferrickharmony.mapper;

import br.com.ferrickharmony.dto.patient.PatientRequestDTO;
import br.com.ferrickharmony.dto.patient.PatientResponseDTO;
import br.com.ferrickharmony.model.Address;
import br.com.ferrickharmony.model.Patient;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDTO dto) {
        if(dto == null) return null;

        Address address = Address.builder()
                .cep(dto.cep())
                .street(dto.street())
                .number(dto.number())
                .complement(dto.complement())
                .neighborhood(dto.neighborhood())
                .city(dto.city())
                .state(dto.state())
                .build();

        return Patient
                .builder()
                .name(dto.name())
                .cpf(dto.cpf())
                .email(dto.email())
                .birthDate(dto.birthDate())
                .phone(dto.phone())
                .secondaryPhone(dto.secondaryPhone())
                .address(address)
                .build();
    }

    public PatientResponseDTO toResponseDTO(Patient entity) {
        if(entity == null) return null;

        return new PatientResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getCpf(),
                entity.getEmail(),
                entity.getBirthDate(),
                entity.getPhone(),
                entity.getSecondaryPhone(),
                entity.getAddress(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

}
